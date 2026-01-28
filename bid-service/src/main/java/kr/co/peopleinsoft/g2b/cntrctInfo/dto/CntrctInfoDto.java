package kr.co.peopleinsoft.g2b.cntrctInfo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CntrctInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 통합계약번호 (계약을 고유하게 식별하는 번호)
	 */
	private String untyCntrctNo;

	/**
	 * 업무구분명 (예: 물품구매, 용역, 공사 등)
	 */
	private String bsnsDivNm;

	/**
	 * 결정계약번호 (계약 결정 시 부여되는 번호)
	 */
	private String dcsnCntrctNo;

	/**
	 * 계약참조번호 (계약 참조용 번호)
	 */
	private String cntrctRefNo;

	/**
	 * 공동계약여부 (Y:공동계약, N:단독계약)
	 */
	private String cmmncNtrctYn;

	/**
	 * 장기계속구분명 (예: 장기계속계약, 일반계약 등)
	 */
	private String lngtrmCtnuDivNm;

	/**
	 * 계약체결일자 (계약서에 서명한 날짜)
	 */
	private String cntrctCnclsDate;

	/**
	 * 계약기간 (계약 시작일부터 종료일까지의 기간)
	 */
	private String cntrctPrd;

	/**
	 * 계약일자 (계약 체결 일자)
	 */
	private String cntrctDate;

	/**
	 * 총계약금액 (원, 전체 계약금액)
	 */
	private BigDecimal totCntrctAmt;

	/**
	 * 당해연도계약금액 (원, 해당 연도 집행 예정 금액)
	 */
	private BigDecimal thtmCntrctAmt;

	/**
	 * 보증금율 (%, 계약보증금 비율)
	 */
	private String grntymnyRate;

	/**
	 * 지체상금율 (%, 계약 지연 시 부과되는 상금 비율)
	 */
	private String dfrcmpnstRt;

	/**
	 * 계약명 (계약 건의 명칭)
	 */
	private String cntrctNm;

	/**
	 * 근거법령명 (계약의 법적 근거가 되는 법령)
	 */
	private String baseLawNm;

	/**
	 * 계약체결방법명 (예: 일반경쟁, 제한경쟁, 수의계약 등)
	 */
	private String cntrctCnclsMthdNm;

	/**
	 * 지급구분명 (예: 선금, 중도금, 준공금 등)
	 */
	private String payDivNm;

	/**
	 * 계약정보URL (계약 정보 조회 URL)
	 */
	private String cntrctInfoUrl;

	/**
	 * 계약상세정보URL (계약 상세 정보 조회 URL)
	 */
	private String cntrctDtlInfoUrl;

	/**
	 * 요청번호 (계약 요청 시 부여되는 번호)
	 */
	private String reqNo;

	/**
	 * 공고번호 (입찰공고번호)
	 */
	private String ntceNo;

	/**
	 * 계약기관코드
	 */
	private String cntrctInsttCd;

	/**
	 * 계약기관명
	 */
	private String cntrctInsttNm;

	/**
	 * 계약기관관할구분명 (예: 중앙, 지방 등)
	 */
	private String cntrctInsttJrsdctnDivNm;

	/**
	 * 계약기관담당부서명
	 */
	private String cntrctInsttChrgDeptNm;

	/**
	 * 계약기관담당자명
	 */
	private String cntrctInsttOfclNm;

	/**
	 * 계약기관담당자전화번호
	 */
	private String cntrctInsttOfclTelNo;

	/**
	 * 계약기관담당자팩스번호
	 */
	private String cntrctInsttOfclFaxNo;

	/**
	 * 수요기관목록 (계약에 포함된 수요기관들의 목록, JSON 또는 구분자로 저장)
	 */
	private String dminsttList;

	/**
	 * 업체목록 (계약에 참여한 업체들의 목록, JSON 또는 구분자로 저장)
	 */
	private String corpList;

	/**
	 * 채권자명 (계약 상대방)
	 */
	private String crdtrNm;

	/**
	 * 기초상세 (계약의 기초가 되는 상세 정보)
	 */
	private String baseDtls;

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

	/**
	 * 공공조달분류번호
	 */
	private String pubPrcrmntClsfcNo;

	/**
	 * 공공조달분류명
	 */
	private String pubPrcrmntClsfcNm;

	/**
	 * 공공조달중분류명
	 */
	private String pubPrcrmntMidClsfcNm;

	/**
	 * 공공조달대분류명
	 */
	private String pubPrcrmntLrgClsfcNm;

	/**
	 * 정보화사업여부 (Y:정보화사업, N:일반사업)
	 */
	private String infoBizYn;

	/**
	 * 공사명 (건설공사의 명칭)
	 */
	private String cnstwkNm;

	/**
	 * 건설현장지역명 (건설공사 현장 위치)
	 */
	private String cnstrsiteRgnNm;

	/**
	 * 공정변경신청기준코드
	 */
	private String prcesChangeAplBssCd;

	/**
	 * 공정변경신청기준명 (공정 변경 시 적용되는 기준)
	 */
	private String prcesChangeAplBssNm;

	/**
	 * 계약시작일자 (계약이 시작되는 날짜)
	 */
	private String cbgnDate;

	/**
	 * 당해연도계약완료일자 (해당 연도 내 완료 예정일)
	 */
	private String thtmCcmpltDate;

	/**
	 * 총계약완료일자 (전체 계약 완료 예정일)
	 */
	private String ttalCcmpltDate;

	/**
	 * 작업시작일자 (실제 작업 시작 날짜, 공사/용역)
	 */
	private String wbgnDate;

	/**
	 * 당해연도공사완료일자 (해당 연도 내 공사 완료 예정일)
	 */
	private String thtmScmpltDate;

	/**
	 * 총공사완료일자 (전체 공사 완료 예정일)
	 */
	private String ttalScmpltDate;

	/**
	 * 추가제품분류번호 (추가 제품 분류 번호)
	 */
	private String adiPrdctClsfcNo;

	/**
	 * 제품분류번호 (물품의 분류 번호)
	 */
	private String prdctClsfcNo;

	/**
	 * 제품식별번호 (제품 고유 식별 번호)
	 */
	private String prdctIdntNo;

	/**
	 * 제품분류번호명 (제품 분류의 명칭)
	 */
	private String prdctClsfcNoNm;

	/**
	 * 한글제품명 (제품의 한글 명칭)
	 */
	private String krnPrdctNm;

	/**
	 * 제품분류번호명및규격 (제품 분류명과 상세 규격)
	 */
	private String prdctClsfcNoNmNdSpec;

	/**
	 * 원산지코드 (제품 원산지 코드)
	 */
	private String orgplceCd;

	/**
	 * 원산지명 (제품 원산지 명칭)
	 */
	private String orgplceNm;

	/**
	 * 수량단가금액 (원, 단위당 가격)
	 */
	private BigDecimal qtyUprcAmt;

	/**
	 * 제품수량 (계약 물품의 수량)
	 */
	private BigDecimal prdctQty;

	/**
	 * 제품금액 (원, 제품 총 금액)
	 */
	private BigDecimal prdctAmt;

	/**
	 * 제품금액통화 (예: KRW, USD 등)
	 */
	private String prdctAmtCrncy;

	/**
	 * 납품조건코드
	 */
	private String dlvryCndtnCd;

	/**
	 * 납품조건명 (예: 본사납품, 지정장소납품 등)
	 */
	private String dlvryCndtnNm;

	/**
	 * 납품일수 (계약 후 납품까지 소요 일수)
	 */
	private Integer dlvrDaynum;

	/**
	 * 납품기한 (납품 완료 기한일)
	 */
	private String dlvrTmlmt;

	/**
	 * 용역일련번호 (용역의 일련번호)
	 */
	private String srvceSno;

	/**
	 * 대표여부 (Y:대표업체, N:구성원업체)
	 */
	private String rprsntYn;

	/**
	 * 용역금액 (원, 용역 계약 금액)
	 */
	private BigDecimal srvceAmt;

	/**
	 * 관련업체명 (계약에 관련된 업체명)
	 */
	private String rltnCorpNm;

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