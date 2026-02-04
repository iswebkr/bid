package kr.co.peopleinsoft.cmmn.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class BidColctHistDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 수집대상아이디
	 */
	private String colctId;

	/**
	 * 수집대상URL
	 */
	private String colctUri;

	/**
	 * 수집내용
	 */
	private String colctDesc;

	/**
	 * 수집시작일자
	 */
	private String colctBgnDt;

	/**
	 * 수집종료일자
	 */
	private String colctEndDt;

	/**
	 * 수집대상페이지수
	 */
	private Integer colctTotPage;

	/**
	 * 수집완료페이지수
	 */
	private Integer colctCmplPage;

	/**
	 * 수집대상카운트수
	 */
	private Integer colctTotCnt;

	/**
	 * 수집완료카운트수
	 */
	private Integer colctCmplCnt;

	/**
	 * 생성일시 (시스템 등록일시)
	 */
	private LocalDateTime createdDt;

	/**
	 * 생성자 (시스템 등록자)
	 */
	private String createdBy;

	/**
	 * 수정일시 (시스템 수정일시)
	 */
	private LocalDateTime updatedDt;

	/**
	 * 수정자 (시스템 수정자)
	 */
	private String updatedBy;

}