package kr.co.peopleinsoft.g2b.orderPlanSttus.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderPlanSttusDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	// ========================================
	// 기본 정보 (복합 PK)
	// ========================================

	/**
	 * 발주계획통합번호 (발주계획의 고유 통합 번호)
	 * PK 1
	 */
	private String orderPlanUntyNo;

	/**
	 * 발주계획일련번호 (발주계획의 일련번호)
	 * PK 2
	 */
	private String orderPlanSno;

	/**
	 * 발주차수 (발주 차수)
	 * PK 3
	 */
	private String orderOrd;

	// ========================================
	// 업무 구분
	// ========================================

	/**
	 * 업무구분코드 (업무 구분 코드)
	 */
	private String bsnsDivCd;

	/**
	 * 업무구분명 (예: 물품, 용역, 공사 등)
	 */
	private String bsnsDivNm;

	/**
	 * 업무유형코드 (업무 유형 코드)
	 */
	private String bsnsTyCd;

	/**
	 * 업무유형명 (업무의 세부 유형)
	 */
	private String bsnsTyNm;

	// ========================================
	// 발주 일정
	// ========================================

	/**
	 * 발주연도 (발주가 예정된 연도, YYYY 형식)
	 */
	private String orderYear;

	/**
	 * 발주월 (발주가 예정된 월, MM 형식)
	 */
	private String orderMnth;

	// ========================================
	// 기관 정보
	// ========================================

	/**
	 * 발주기관코드 (발주를 담당하는 기관의 코드)
	 */
	private String orderInsttCd;

	/**
	 * 발주기관명 (발주를 담당하는 기관명)
	 */
	private String orderInsttNm;

	/**
	 * 총괄기관명 (상위 총괄 기관명)
	 */
	private String totlmngInsttNm;

	/**
	 * 관할구분코드 (관할 구분 코드)
	 */
	private String jrsdctnDivCd;

	/**
	 * 관할구분명 (예: 중앙, 지방 등)
	 */
	private String jrsdctnDivNm;

	// ========================================
	// 사업 정보
	// ========================================

	/**
	 * 사업명 (발주 사업의 명칭)
	 */
	private String bizNm;

	/**
	 * 조달방법 (예: 일반경쟁, 제한경쟁, 수의계약 등)
	 */
	private String prcrmntMethd;

	/**
	 * 계약방법명 (계약 체결 방법)
	 */
	private String cntrctMthdNm;

	// ========================================
	// 건설공사 관련
	// ========================================

	/**
	 * 공사지역명 (공사가 진행될 지역명, 건설공사)
	 */
	private String cnstwkRgnNm;

	/**
	 * 공종구분명 (공종의 구분, 건설공사)
	 */
	private String cnsttyDivNm;

	/**
	 * 공사관리번호 (공사 관리를 위한 번호, 건설공사)
	 */
	private String cnstwkMngNo;

	/**
	 * 공사기간내용 (공사 예정 기간 설명, 건설공사)
	 */
	private String cnstwkPrdCntnts;

	// ========================================
	// 금액 정보
	// ========================================

	/**
	 * 발주계약금액 (원, 발주 예정 계약금액)
	 */
	private BigDecimal orderContrctAmt;

	/**
	 * 발주관급자재비 (원, 관급자재 비용)
	 */
	private BigDecimal orderGovsplyMtrcst;

	/**
	 * 발주기타금액 (원, 기타 비용)
	 */
	private BigDecimal orderEtcAmt;

	/**
	 * 합계발주금액 (원, 전체 발주금액 합계)
	 */
	private BigDecimal sumOrderAmt;

	/**
	 * 합계발주달러금액 (달러, 외화 발주금액)
	 */
	private BigDecimal sumOrderDolAmt;

	/**
	 * 발주당해계약금액 (원, 당해연도 계약금액)
	 */
	private BigDecimal orderThtmContrctAmt;

	/**
	 * 발주차년도보조금액 (원, 차년도 보조금액)
	 */
	private BigDecimal orderNtntrsAuxAmt;

	// ========================================
	// 담당자 정보
	// ========================================

	/**
	 * 부서명 (담당 부서명)
	 */
	private String deptNm;

	/**
	 * 담당자명 (담당자 성명)
	 */
	private String ofclNm;

	/**
	 * 전화번호 (담당자 전화번호)
	 */
	private String telNo;

	// ========================================
	// 공고 관련
	// ========================================

	/**
	 * 협정여부 (Y:협정대상, N:일반)
	 */
	private String agrmntYn;

	/**
	 * 공고예정여부 (Y:공고예정, N:미정)
	 */
	private String ntceNticeYn;

	// ========================================
	// 사용 및 수량
	// ========================================

	/**
	 * 사용내용 (발주 물품/용역의 사용 목적 및 내용)
	 */
	private String usgCntnts;

	/**
	 * 수량내용 (발주 수량 설명)
	 */
	private String qtyCntnts;

	/**
	 * 단위 (예: EA, SET, M, ㎡ 등)
	 */
	private String unit;

	// ========================================
	// 제품 분류
	// ========================================

	/**
	 * 제품분류번호 (물품의 분류 번호)
	 */
	private String prdctClsfcNo;

	/**
	 * 상세제품분류번호 (물품의 상세 분류 번호)
	 */
	private String dtilPrdctClsfcNo;

	/**
	 * 제품분류번호명 (제품 분류의 명칭)
	 */
	private String prdctClsfcNoNm;

	/**
	 * 상세제품분류번호명 (상세 제품 분류의 명칭)
	 */
	private String dtilPrdctClsfcNoNm;

	/**
	 * 모집등록번호 (모집 관련 등록번호)
	 */
	private String rcritRgstNo;

	// ========================================
	// 규격 항목 (1~5)
	// ========================================

	/**
	 * 규격항목명1 (규격 항목의 명칭1)
	 */
	private String specItemNm1;

	/**
	 * 규격항목명2 (규격 항목의 명칭2)
	 */
	private String specItemNm2;

	/**
	 * 규격항목명3 (규격 항목의 명칭3)
	 */
	private String specItemNm3;

	/**
	 * 규격항목명4 (규격 항목의 명칭4)
	 */
	private String specItemNm4;

	/**
	 * 규격항목명5 (규격 항목의 명칭5)
	 */
	private String specItemNm5;

	/**
	 * 규격항목내용1 (규격 항목의 상세 내용1)
	 */
	private String specItemCntnts1;

	/**
	 * 규격항목내용2 (규격 항목의 상세 내용2)
	 */
	private String specItemCntnts2;

	/**
	 * 규격항목내용3 (규격 항목의 상세 내용3)
	 */
	private String specItemCntnts3;

	/**
	 * 규격항목내용4 (규격 항목의 상세 내용4)
	 */
	private String specItemCntnts4;

	/**
	 * 규격항목내용5 (규격 항목의 상세 내용5)
	 */
	private String specItemCntnts5;

	/**
	 * 규격내용 (전체 규격 내용)
	 */
	private String specCntnts;

	// ========================================
	// 예산 및 공고
	// ========================================

	/**
	 * 예산구분코드 (예산 구분 코드)
	 */
	private String bdgtDivCd;

	/**
	 * 공고일자 (발주 공고 예정일)
	 */
	private String nticeDt;

	// ========================================
	// 설계도서 (건설공사)
	// ========================================

	/**
	 * 설계도서열람장소명 (설계도서를 열람할 수 있는 장소, 건설공사)
	 */
	private String dsgnDocRdngPlceNm;

	/**
	 * 설계도서열람기간내용 (설계도서 열람 가능 기간, 건설공사)
	 */
	private String dsgnDocRdngPrdCntnts;

	// ========================================
	// 기타
	// ========================================

	/**
	 * 비고내용 (기타 특이사항)
	 */
	private String rmrkCntnts;

	/**
	 * 입찰공고번호목록 (연관된 입찰공고번호 목록, JSON 또는 구분자로 저장)
	 */
	private String bidNtceNoList;

	// ========================================
	// 시스템 정보
	// ========================================

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

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