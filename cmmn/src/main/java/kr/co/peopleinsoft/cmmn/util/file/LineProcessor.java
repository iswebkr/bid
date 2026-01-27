package kr.co.peopleinsoft.cmmn.util.file;

@FunctionalInterface
public interface LineProcessor {
	<T> T process(String line, String separator) throws Exception;
}