package kr.co.peopleinsoft.g2b.userInfo.dto.dminsttInfo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class DminsttInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;


	// ========================================
	// 기관 기본 정보
	// ========================================

	/**
	 * 수요기관코드 (기관을 고유하게 식별하는 코드)
	 * PK
	 */
	private String dminsttCd;

	/**
	 * 수요기관명 (기관의 정식 명칭)
	 */
	private String dminsttNm;

	/**
	 * 수요기관약어명 (기관의 약칭)
	 */
	private String dminsttAbrvtNm;

	/**
	 * 수요기관영문명 (기관의 영문 명칭)
	 */
	private String dminsttEngNm;

	// ========================================
	// 유효기간
	// ========================================

	/**
	 * 유효기간시작일자 (기관 정보 유효 시작일)
	 */
	private String vldPrdBgnDt;

	/**
	 * 유효기간종료일자 (기관 정보 유효 종료일)
	 */
	private String vldPrdEndDt;

	// ========================================
	// 사업자 정보
	// ========================================

	/**
	 * 법인등록번호 (법인 등록 시 부여되는 번호)
	 */
	private String corprtRgstNo;

	/**
	 * 사업자번호 (사업자등록번호, 10자리)
	 */
	private String bizno;

	// ========================================
	// 관할 및 분류
	// ========================================

	/**
	 * 관할구분명 (예: 중앙행정기관, 지방자치단체, 공공기관 등)
	 */
	private String jrsdctnDivNm;

	/**
	 * 기관유형코드대분류명 (기관 유형의 대분류)
	 */
	private String insttTyCdLrgclsfcNm;

	/**
	 * 기관유형코드중분류명 (기관 유형의 중분류)
	 */
	private String insttTyCdMidclsfcNm;

	/**
	 * 기관유형코드소분류명 (기관 유형의 소분류)
	 */
	private String insttTyCdSmlclsfcNm;

	// ========================================
	// 업태 및 업종
	// ========================================

	/**
	 * 업태명 (사업자의 업태, 예: 서비스업, 제조업 등)
	 */
	private String bizcndtnNm;

	/**
	 * 업종명 (사업자의 업종)
	 */
	private String indstrytyNm;

	// ========================================
	// 연락처 정보
	// ========================================

	/**
	 * 공식팩스번호 (기관의 대표 팩스번호)
	 */
	private String ofclFaxNo;

	/**
	 * 전화번호 (기관의 대표 전화번호)
	 */
	private String telNo;

	/**
	 * 팩스번호 (기관의 팩스번호)
	 */
	private String faxNo;

	// ========================================
	// 주소 정보
	// ========================================

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

	// ========================================
	// 홈페이지
	// ========================================

	/**
	 * 홈페이지주소 (기관의 공식 웹사이트 URL)
	 */
	private String hmpgAdrs;

	// ========================================
	// 삭제 여부
	// ========================================

	/**
	 * 삭제여부 (Y:삭제됨, N:사용중)
	 */
	private String dltYn;

	// ========================================
	// 최상위 기관 정보
	// ========================================

	/**
	 * 최상위기관코드 (상위 소속 기관의 코드)
	 */
	private String toplvlInsttCd;

	/**
	 * 최상위기관명 (상위 소속 기관의 명칭)
	 */
	private String toplvlInsttNm;

	// ========================================
	// 등록 정보 (조달청 시스템)
	// ========================================

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

	// ========================================
	// 시스템 관리 컬럼
	// ========================================

	/**
	 * 생성자 (시스템 등록자)
	 */
	private String createdBy;

	/**
	 * 수정자 (시스템 수정자)
	 */
	private String updatedBy;
}