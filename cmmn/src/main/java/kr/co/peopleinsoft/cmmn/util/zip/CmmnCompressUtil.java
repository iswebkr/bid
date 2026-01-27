package kr.co.peopleinsoft.cmmn.util.zip;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.http.HttpHeaders;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

public class CmmnCompressUtil {

	public static void compressFile(String sourceFile, String zipfile) throws IOException {
		zipSingleFile(sourceFile, zipfile, 6);
	}

	public static void compressFile(String sourceFile, String zipfile, int level) throws IOException {
		zipSingleFile(sourceFile, zipfile, level);
	}

	public static void compressMultipleFile(String[] sourcefiles, String zipfile) throws IOException {
		zipMultipleFiles(sourcefiles, zipfile, 6);
	}

	public static void compressMultipleFile(String[] sourcefiles, String zipfile, int level) throws IOException {
		zipMultipleFiles(sourcefiles, zipfile, level);
	}

	public static void compressDirectory(String sourceDir, String zipfile) throws IOException {
		zipDirectory(sourceDir, zipfile, 6);
	}

	public static void compressDirectory(String sourceDir, String zipfile, int level) throws IOException {
		zipDirectory(sourceDir, zipfile, level);
	}

	public void download(HttpServletResponse response, String zipFile) {
		downloadZipFile(response, zipFile, 1024);
	}

	public void downloadZip(HttpServletResponse response, String zipFile, int bufferSize) {
		downloadZipFile(response, zipFile, bufferSize);
	}

	// 단일파일 압축
	static void zipSingleFile(String sourceFile, String zipFile, int level) throws IOException {

		Path path = Paths.get(zipFile);
		Files.createDirectories(path.getParent());

		try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536);
		     ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(bufferedOutputStream)) {
			initZipOutputStream(zipOutputStream, level);
			File fileToZip = new File(sourceFile);

			try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(fileToZip.getName());
				zipArchiveEntry.setSize(sourceFile.length());
				zipOutputStream.putArchiveEntry(zipArchiveEntry);
				fileInputStream.transferTo(zipOutputStream);
				zipOutputStream.closeArchiveEntry();
			}
		}
	}

	// 다중파일압축
	static void zipMultipleFiles(String[] sourceFiles, String zipFile, int level) throws IOException {
		Path path = Paths.get(zipFile);
		Files.createDirectories(path.getParent());

		try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536);
		     ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(bufferedOutputStream)) {

			initZipOutputStream(zipOutputStream, level);

			for (String sourceFile : sourceFiles) {
				File fileToZip = new File(sourceFile);

				try (FileInputStream fileInputStream = new FileInputStream(sourceFile)) {
					ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(fileToZip.getName());
					zipArchiveEntry.setSize(sourceFile.length());
					zipOutputStream.putArchiveEntry(zipArchiveEntry);
					fileInputStream.transferTo(zipOutputStream);
					zipOutputStream.closeArchiveEntry();
				}
			}
		}
	}

	// 디렉토리 압축
	static void zipDirectory(String sourceDir, String zipFile, int level) throws IOException {
		Path path = Paths.get(zipFile);
		Files.createDirectories(path.getParent());

		try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536);
		     ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(bufferedOutputStream)) {
			initZipOutputStream(zipOutputStream, level);
			File sourceFile = new File(sourceDir);
			recursiveZipDirectory(sourceFile, sourceFile, zipOutputStream);
		}
	}

	// 디렉토리를 순환하며 모든 파일 압축
	static void recursiveZipDirectory(File fileToZip, File baseDir, ZipArchiveOutputStream zipOutputStream) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}

		Path basePath = Paths.get(baseDir.getAbsolutePath());
		Path filePath = Paths.get(fileToZip.getAbsolutePath());
		String relativePath = basePath.relativize(filePath).toString();

		String zipPath = relativePath.replace("\\", "/");

		if (fileToZip.isDirectory()) {
			if (!zipPath.endsWith("/")) {
				zipPath += "/";
			}

			// Directory ArchiveEntry 추가
			ZipArchiveEntry directoryEntry = new ZipArchiveEntry(zipPath);
			zipOutputStream.putArchiveEntry(directoryEntry);
			zipOutputStream.closeArchiveEntry();

			File[] children = fileToZip.listFiles();
			if (children != null) {
				for (File childFile : children) {
					recursiveZipDirectory(childFile, baseDir, zipOutputStream);
				}
			}
		} else {
			try (FileInputStream fileInputStream = new FileInputStream(fileToZip)) {
				ZipArchiveEntry fileEntry = new ZipArchiveEntry(zipPath);
				fileEntry.setSize(fileToZip.length());
				zipOutputStream.putArchiveEntry(fileEntry);

				fileInputStream.transferTo(zipOutputStream);
				zipOutputStream.closeArchiveEntry();
			}
		}
	}

	// 압축파일 다운로드
	static void downloadZipFile(HttpServletResponse response, String zipFile, int bufferSize) {
		String downloadFileName = "archive_" + System.currentTimeMillis() + ".zip";

		File file = new File(zipFile);
		response.setContentType("application/zip");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"");
		response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

		try (FileInputStream fileInputStream = new FileInputStream(zipFile)) {
			OutputStream outputStream = response.getOutputStream();
			byte[] buffer = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// ZipOutputStream 설정
	static void initZipOutputStream(ZipArchiveOutputStream zipOutputStream, int level) {
		/*
		 * 압축레벨설정
		 * 0 : 무압축(STORED)
		 * 1-9 : 압축(DEFLATED), 1=최소압축/빠름, 9=최대압축/느림
		 * -1 : 기본값(보통6)
		 */
		zipOutputStream.setLevel(level); // 압축레벨 (0~9)
		zipOutputStream.setEncoding("UTF-8"); // 인코딩설정(한글파일)
		zipOutputStream.setUseZip64(Zip64Mode.AsNeeded); // 4GB이상 파일 처리
	}

	// 압축 해제
	public static void extractCompress(String zipFile, String destinationDirectory) throws IOException {
		File destDir = new File(destinationDirectory);
		if (!destDir.exists()) {
			Files.createDirectories(destDir.toPath());
		}

		try (ZipFile zip = ZipFile.builder()
			.setFile(new File(zipFile))
			.setCharset(StandardCharsets.UTF_8)
			.get()) {

			Enumeration<ZipArchiveEntry> entries = zip.getEntries();

			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				String filePath = destinationDirectory + File.separator + entry.getName();

				// Path Traversal 방지
				Path destPath = Paths.get(destinationDirectory).normalize();
				Path entryPath = Paths.get(filePath).normalize();
				if (!entryPath.startsWith(destPath)) {
					throw new IOException("Entry is outside of the target dir: " + entry.getName());
				}

				if (!entry.isDirectory()) {
					Files.createDirectories(new File(filePath).getParentFile().toPath());
					try (InputStream in = zip.getInputStream(entry);
					     OutputStream out = Files.newOutputStream(Paths.get(filePath))) {
						in.transferTo(out);
					}
				} else {
					Files.createDirectories(new File(filePath).toPath());
				}
			}
		}
	}
}