package kr.co.peopleinsoft.mois.stanOrgCd.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class StanOrgCdDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 기관코드
	 */
	private String orgCd;

	/**
	 * 기관명전체
	 */
	private String fullNm;

	/**
	 * 기관명최하위
	 */
	private String lowNm;

	/**
	 * 기관명약어
	 */
	private String abbrNm;

	/**
	 * 차수
	 */
	private String gapNo;

	/**
	 * 서열
	 */
	private String rankNo;

	/**
	 * 소속기관차수
	 */
	private String subChasu;

	/**
	 * 상위기관코드
	 */
	private String highCd;

	/**
	 * 최상위기관코
	 */
	private String highstCd;

	/**
	 * 대표기관코드
	 */
	private String repCd;

	/**
	 * 기관대분류
	 */
	private String typebigNm;

	/**
	 * 기관중분류
	 */
	private String typemidNm;

	/**
	 * 기관소분류
	 */
	private String typesmlNm;

	/**
	 * 소재지코드
	 */
	private String locatstdCd;

	/**
	 * 현행기관코드
	 */
	private String useCd;

	/**
	 * 생성일
	 */
	private String crtDe;

	/**
	 * 폐지일
	 */
	private String clsDe;

	/**
	 * 폐지구분
	 */
	private String stopSelt;

	/**
	 * 변경일
	 */
	private String chgDe;

	/**
	 * 기초일자
	 */
	private String baseDate;

	/**
	 * 적용일
	 */
	private String adptDate;

	/**
	 * 이전기관코드
	 */
	private String preorgCd;

	/**
	 * 생성자 (시스템 등록자)
	 */
	private String createdBy;

	/**
	 * 수정자 (시스템 수정자)
	 */
	private String updatedBy;

}