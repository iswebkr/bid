package kr.co.peopleinsoft.g2b.dto.cmmn;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class BidColctHistResultDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 수집대상아이디
	 */
	private String colctId;

	/**
	 * 수집시작일자
	 */
	private String colctBgnDt;

	/**
	 * 수집종료일자
	 */
	private String colctEndDt;

	/**
	 * 전체 페이지수
	 */
	private Integer maxTotPage;

	/**
	 * 수집 완료 페이지수
	 */
	private Integer cmplColctPage;

	/**
	 * 전체 카운트
	 */
	private Integer maxTotalCnt;

	/**
	 * 수집된 데이터 카운트
	 */
	private Integer sumCmplCnt;

	/**
	 * 수집 상태
	 */
	private String colctState;

	/**
	 * 차이나는 카운트
	 */
	private Integer diffCount;

	/**
	 * 차이나는 페이지
	 */
	private Integer diffPage;

}