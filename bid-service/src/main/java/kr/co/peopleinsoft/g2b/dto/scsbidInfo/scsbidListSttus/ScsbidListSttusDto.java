package kr.co.peopleinsoft.g2b.dto.scsbidInfo.scsbidListSttus;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 낙찰목록
 */
@Getter
@Setter
public class ScsbidListSttusDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 입찰공고번호
	 */
	private String bidNtceNo;

	/**
	 * 입찰공고차수
	 */
	private String bidNtceOrd;

	/**
	 * 입찰분류번호 (예: 물품, 용역, 공사 등 구분)
	 */
	private String bidClsfcNo;

	/**
	 * 재입찰번호 (재입찰 시 부여되는 일련번호)
	 */
	private String rbidNo;

	/**
	 * 공고구분코드 (예: 공개경쟁, 제한경쟁 등)
	 */
	private String ntceDivCd;

	/**
	 * 입찰공고명
	 */
	private String bidNtceNm;

	/**
	 * 참가업체수 (해당 입찰에 참가한 총 업체 수)
	 */
	private Integer prtcptCnum;

	/**
	 * 낙찰자명 (낙찰받은 업체 또는 개인의 명칭)
	 */
	private String bidwinnrNm;

	/**
	 * 낙찰자사업자등록번호 (10자리 또는 13자리)
	 */
	private String bidwinnrBizno;

	/**
	 * 낙찰자대표자명 (낙찰 업체의 대표자 성명)
	 */
	private String bidwinnrCeoNm;

	/**
	 * 낙찰자주소 (낙찰 업체의 사업장 주소)
	 */
	private String bidwinnrAdrs;

	/**
	 * 낙찰자전화번호
	 */
	private String bidwinnrTelNo;

	/**
	 * 낙찰금액 (원, 최종 낙찰된 금액)
	 */
	private BigDecimal sucsfbidAmt;

	/**
	 * 낙찰률 (예정가격 대비 낙찰금액 비율, 소수점 4자리)
	 */
	private BigDecimal sucsfbidRate;

	/**
	 * 실제개찰일시 (실제로 개찰이 진행된 일시)
	 */
	private String rlOpengDt;

	/**
	 * 최종낙찰일자 (최종 낙찰이 확정된 날짜)
	 */
	private String fnlSucsfDate;

	/**
	 * 수요기관코드
	 */
	private String dminsttCd;

	/**
	 * 수요기관명
	 */
	private String dminsttNm;

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 최종낙찰업체담당자 (낙찰 업체의 담당자명)
	 */
	private String fnlSucsfCorpOfcl;

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