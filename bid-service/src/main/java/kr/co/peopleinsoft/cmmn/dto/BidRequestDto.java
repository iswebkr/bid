package kr.co.peopleinsoft.cmmn.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class BidRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String serviceKey; // 공공데이터포털에서 받은 인증키
	private String serviceId; // API ID
	private String serviceDescription; // API 설명
	private int pageNo; // 페이지번호
	private int numOfRows; // 한 페이지 결과 수 (최대 100)
	private int inqryDiv; // 조회구분
	private String type; // 타입(json / xml)
	private String bidType; // 공사/용역/외자 등 한글로 작성

	private String inqryBgnDt; // 조회기준시작일시 (yyyyMMddHHmm)
	private String inqryEndDt; // 조회기준종료일시 (yyyyMMddHHmm)

	private int totalPage;
	private int totalCount;
}