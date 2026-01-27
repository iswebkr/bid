package kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengComptList;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OpengComptResultListInfoDto implements Serializable {

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
	 * 개찰순위 (1위, 2위, 3위 등 투찰 순위)
	 */
	private Integer opengRank;

	/**
	 * 개찰결과구분명 (예: 낙찰, 유찰, 실격 등)
	 */
	private String opengRsltDivNm;

	/**
	 * 입찰분류번호 (예: 0=일반입찰)
	 */
	private String bidClsfcNo;

	/**
	 * 재입찰번호 (예: 000=최초입찰, 001=재입찰)
	 */
	private String rbidNo;

	/**
	 * 투찰자사업자번호 (입찰에 참여한 업체의 사업자등록번호, 10자리)
	 */
	private String prcbdrBizno;

	/**
	 * 투찰자명 (입찰에 참여한 업체명 또는 개인명)
	 */
	private String prcbdrNm;

	/**
	 * 투찰자대표자명 (투찰 업체의 대표자 성명)
	 */
	private String prcbdrCeoNm;

	/**
	 * 투찰금액 (원, 투찰자가 제시한 입찰 금액)
	 */
	private BigDecimal bidprcAmt;

	/**
	 * 투찰률 (예정가격 대비 투찰금액 비율, %, 소수점 2자리)
	 */
	private BigDecimal bidprcrt;

	/**
	 * 비고 (특이사항, 실격사유 등)
	 */
	private String rmrk;

	/**
	 * 공종분담투찰금액URL (건설공사의 공종별 분담 금액 상세 URL)
	 */
	private String cnsttyAccotBidAmtUrl;

	/**
	 * 추첨번호1 (예비가격 추첨 시 첫 번째 추첨 번호)
	 */
	private String drwtNo1;

	/**
	 * 추첨번호2 (예비가격 추첨 시 두 번째 추첨 번호)
	 */
	private String drwtNo2;

	/**
	 * 투찰일시 (투찰자가 입찰가격을 제출한 일시)
	 */
	private String bidprcDt;

	/**
	 * 입찰가격평가값 (가격 평가 점수, 소수점 4자리)
	 */
	private BigDecimal bidPrceEvlVal;

	/**
	 * 기술평가값 (기술 평가 점수, 소수점 4자리)
	 */
	private BigDecimal techEvlVal;

	/**
	 * 종합평가금액값 (가격+기술 종합 평가 점수, 소수점 4자리)
	 */
	private BigDecimal totalEvlAmtVal;

	/**
	 * 기술평가성질값 (기술평가의 성격 또는 등급)
	 */
	private String techEvlNaturVal;

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