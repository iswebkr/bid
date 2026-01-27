package kr.co.peopleinsoft.cmmn.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CmmnFileDownload {
	static final Logger logger = LoggerFactory.getLogger(CmmnFileDownload.class);

	protected static final long BUFFER_SIZE = 1024 * 1024;

	public ResponseEntity<?> download(String fileName, String downloadFileName) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			Path filePath = Paths.get(fileName);

			if (!Files.exists(filePath)) {
				return ResponseEntity.notFound().build();
			}

			String contentType = Files.probeContentType(filePath);
			if (contentType == null) {
				contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			long fileSize = Files.size(filePath);

			if (logger.isInfoEnabled()) {
				logger.info("Starting download : {} ({}MB)", fileName, fileSize / 1024 / 1024);
			}

			StreamingResponseBody streamingResponseBody = outputStream -> {
				try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
					long position = 0;
					long totalTransferred = 0;

					while (totalTransferred < fileSize) {
						long count = Math.min(BUFFER_SIZE, fileSize - totalTransferred);
						long actualTransferred = fileChannel.transferTo(position, count, Channels.newChannel(outputStream));

						if (actualTransferred <= 0) {
							break;
						}

						position += actualTransferred;
						totalTransferred += actualTransferred;

						if (totalTransferred % (10L * (1024 * 1024)) == 0) {
							if (logger.isInfoEnabled()) {
								logger.info("Download progress : {}MB / {}MB", totalTransferred / 1024 / 1024, fileSize / 1024 / 1024);
							}
						}
					}
					outputStream.flush();

					stopWatch.stop();

					if (logger.isInfoEnabled()) {
						logger.info("Download completed : {} in {}ms", fileName, stopWatch.getTotalTimeMillis());
					}
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Download failed : {} - {}", fileName, e.getMessage());
					}
					throw new RuntimeException("An error occurred while streaming the file", e);
				}
			};

			String encodingFileName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8);

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", downloadFileName, encodingFileName));
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
			headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

			return ResponseEntity.ok().headers(headers).body(streamingResponseBody);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Download initialization failed: {} - {}", fileName, e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}