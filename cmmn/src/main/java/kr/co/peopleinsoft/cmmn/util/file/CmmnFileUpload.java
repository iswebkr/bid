package kr.co.peopleinsoft.cmmn.util.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CmmnFileUpload {

	final static Logger logger = LoggerFactory.getLogger(CmmnFileUpload.class);

	static final String UPLOAD_PATH = "/upload";
	static final int BUFFER_SIZE = 8192; // 8KB 버퍼 크기
	static final int MAX_RETRY_ATTEMPTS  = 3; // 업로드 재시도 횟수
	static final Path TEMP_PATH = Paths.get(System.getProperty("java.io.tmpdir"));

	public UploadResult upload(MultipartFile file) throws IOException, InterruptedException {
		FileMetadata fileMetadata = getFileMetadata(file);

		if (fileMetadata.validFile) {
			createUploadDirectory(fileMetadata);

			logger.info("File upload start : {} (Size : {})", fileMetadata.getFileName(), FileUtils.byteCountToDisplaySize(fileMetadata.getFileSize()));

			for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
				StreamingResult streamingResult = saveFileWithStreaming(file, fileMetadata, attempt, (result) -> {
					try {
						if (result.isSuccess()) {
							Files.createDirectories(fileMetadata.getSaveFilePath().getParent());
							Files.move(fileMetadata.getTempFilePath(), fileMetadata.getSaveFilePath(), StandardCopyOption.REPLACE_EXISTING);

							Files.deleteIfExists(fileMetadata.getTempFilePath());

							if (logger.isInfoEnabled()) {
								logger.info("file upload completed {} -> {}", fileMetadata.getFileName(), fileMetadata.getSaveFileName());
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});

				if (streamingResult.isSuccess()) {
					return UploadResult.builder()
						.status("success")
						.message("file upload successful")
						.build();
				} else {
					if (attempt < MAX_RETRY_ATTEMPTS) {
						Thread.sleep(1000 * attempt);
					}
				}
			}
		}

		return UploadResult.builder()
			.status("failure")
			.message(fileMetadata.getValidateMessage()).build();
	}

	public CompletableFuture<UploadResult> asyncUpload(MultipartFile file, Consumer<UploadResult> onSuccess, Consumer<UploadResult> onError) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return upload(file);
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}, Executors.newFixedThreadPool(5)).whenComplete((result, throwable) -> {
			if (throwable != null) {
				onError.accept(result);
			} else {
				onSuccess.accept(result);
			}
		});
	}

	FileMetadata getFileMetadata(MultipartFile file) throws IOException {
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.generateSeed(20);

		if (file != null && file.getOriginalFilename() != null && file.getSize() > 0) {
			Path tempFilePath = Files.createTempFile(TEMP_PATH, "upload_", ".tmp");
			String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
			String month = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM"));
			String day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd"));
			String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
			String encodeFileName = Base64.getEncoder().encodeToString(file.getOriginalFilename().getBytes(StandardCharsets.UTF_8));
			Path targetUploadPath = Paths.get(UPLOAD_PATH, year, month, day, time);
			targetUploadPath = targetUploadPath.resolve(encodeFileName);

			return FileMetadata.builder()
				.fileName(file.getOriginalFilename())
				.fileSize(file.getSize())
				.fileExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
				.saveFileName(encodeFileName)
				.saveFilePath(targetUploadPath)
				.contentType(file.getContentType())
				.tempFilePath(tempFilePath)
				.validFile(true)
				.validateMessage("업로드 가능한 파일")
				.build();
		}
		return FileMetadata.builder()
			.validFile(false)
			.validateMessage("없는 파일이거나 비어있는 파일은 업로드가 불가합니다.").build();
	}

	void createUploadDirectory(FileMetadata fileMetadata) {
		try {
			if (!Files.exists(fileMetadata.getSaveFilePath())) {
				Files.createDirectories(fileMetadata.getSaveFilePath());
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("An error occurred while creating upload directory. ({})", UPLOAD_PATH);
			}
		}
	}

	StreamingResult saveFileWithStreaming(MultipartFile file, FileMetadata fileMetadata, int attempt, Consumer<StreamingResult> uploadCallback) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			try (InputStream inputStream = file.getInputStream();
			     BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
			     FileChannel fileChannel = FileChannel.open(fileMetadata.getTempFilePath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

				byte[] buffer = new byte[BUFFER_SIZE];
				long totalBytesWritten = 0;
				int bytesRead;

				while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
					messageDigest.update(buffer, 0, bytesRead);

					ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
					int bytesWritten = fileChannel.write(byteBuffer);

					if (bytesWritten != bytesRead) {
						throw new IOException("Expected to write " + bytesRead + " bytes, but wrote " + bytesWritten);
					}

					totalBytesWritten += bytesWritten;

					if (totalBytesWritten % (100 * 1024 * 1024) == 0) {
						logger.debug("File writing progress : {} ({})", FileUtils.byteCountToDisplaySize(totalBytesWritten), fileMetadata.getFileName());
					}
				}

				fileChannel.force(true);

				StringBuilder checksum = new StringBuilder();
				for (byte b : messageDigest.digest()) {
					checksum.append(String.format("%02x", b));
				}

				logger.info("File streaming save complete : {} bytes written, checksum: {}, attempt : {}", totalBytesWritten, checksum, attempt);

				StreamingResult streamingResult = StreamingResult.builder()
					.success(true)
					.bytesWritten(totalBytesWritten)
					.checksum(checksum.toString())
					.build();

				uploadCallback.accept(streamingResult);

				return streamingResult;
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			if (logger.isErrorEnabled()) {
				logger.error("An error occurred while saving streaming. (attempt {}): {}", attempt, e.getMessage());
			}
			return StreamingResult.builder()
				.success(false)
				.message("An error occurred while saving streaming. " + e.getMessage())
				.build();
		}
	}

	@Getter
	@Setter
	@Builder
	public static class UploadResult {
		private String status;
		private String message;
		private String remoteUrl;
		private FileMetadata fileMetadata;
	}

	@Getter
	@Setter
	@Builder
	public static class FileMetadata {
		private String fileName;
		private long fileSize;
		private String saveFileName;
		private Path saveFilePath;
		private String fileExtension;
		private String contentType;
		private Path tempFilePath;
		private boolean validFile;
		private String validateMessage;
	}

	@Getter
	@Setter
	@Builder
	public static class StreamingResult {
		private boolean success;
		private long bytesWritten;
		private String checksum;
		private String message;
	}
}