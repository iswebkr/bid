package kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class PrcrmntCorpBasicInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 사업자번호 (업체를 고유하게 식별하는 사업자등록번호, 10자리)
	 */
	private String bizno;

	/**
	 * 업체명 (업체의 정식 명칭)
	 */
	private String corpNm;

	/**
	 * 영문업체명 (업체의 영문 명칭)
	 */
	private String engCorpNm;

	/**
	 * 대표자명 (업체 대표자의 성명)
	 */
	private String ceoNm;

	/**
	 * 개업일자 (사업자등록증에 기재된 개업일자)
	 */
	private String opbizDt;

	/**
	 * 지역코드 (행정구역 코드)
	 */
	private String rgnCd;

	/**
	 * 지역명 (행정구역명, 예: 서울특별시, 경기도 등)
	 */
	private String rgnNm;

	/**
	 * 우편번호 (5자리 또는 6자리)
	 */
	private String zip;

	/**
	 * 주소 (기본 주소)
	 */
	private String adrs;

	/**
	 * 상세주소 (상세 주소)
	 */
	private String dtlAdrs;

	/**
	 * 전화번호 (업체의 대표 전화번호)
	 */
	private String telNo;

	/**
	 * 팩스번호 (업체의 팩스번호)
	 */
	private String faxNo;

	/**
	 * 국가명 (업체가 소재한 국가, 예: 대한민국, 미국 등)
	 */
	private String cntryNm;

	/**
	 * 홈페이지주소 (업체의 공식 웹사이트 URL)
	 */
	private String hmpgAdrs;

	/**
	 * 제조구분코드 (제조업체 구분 코드)
	 */
	private String mnfctDivCd;

	/**
	 * 제조구분명 (예: 제조업체, 판매업체, 제조판매업체 등)
	 */
	private String mnfctDivNm;

	/**
	 * 직원수 (업체의 총 직원 수)
	 */
	private Integer emplyeNum;

	/**
	 * 업체업무구분코드 (업체의 주요 업무 구분 코드)
	 */
	private String corpBsnsDivCd;

	/**
	 * 업체업무구분명 (예: 물품, 용역, 공사 등)
	 */
	private String corpBsnsDivNm;

	/**
	 * 본사구분명 (예: 본사, 지사, 사업소 등)
	 */
	private String hdoffceDivNm;

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

	/**
	 * 필수인증등록여부 (Y:필수인증등록됨, N:미등록)
	 */
	private String esntlNoCertRgstYn;

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