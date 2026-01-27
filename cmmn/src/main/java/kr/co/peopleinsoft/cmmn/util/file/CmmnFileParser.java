package kr.co.peopleinsoft.cmmn.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class CmmnFileParser {

	static final Logger logger = LoggerFactory.getLogger(CmmnFileParser.class);

	static String strSeparator = "|,;\t";

	public static void processInChunks(String fileName, int chunkSize, LineProcessor lineProcessor) throws FileNotFoundException {
		processInChunks(fileName, chunkSize, strSeparator, lineProcessor);
	}

	public static void processInChunks(String fileName, int chunkSize, String separator, LineProcessor lineProcessor) throws FileNotFoundException {
		Path path = Paths.get(fileName);
		AtomicLong processedLines = new AtomicLong();
		strSeparator = separator;

		if (!Files.exists(path)) {
			throw new FileNotFoundException("대상 파일이 존재하지 않습니다.");
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
			lines.filter(line -> !line.trim().isEmpty()).forEach(line -> {
				// 라인별 처리
				try {
					long count = processedLines.incrementAndGet();
					if (count % chunkSize == 0) {
						logger.info("Number of processed lines : {}", count);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		stopWatch.stop();
		logger.info("TotalTimeSeconds : {} Seconds", stopWatch.getTotalTimeSeconds());
	}
}