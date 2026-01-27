package kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengResultPreparPcDetail;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OpengResultPreparPcDetailDto implements Serializable {

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
	 * 입찰분류번호 (예: 0=일반입찰)
	 */
	private String bidClsfcNo;

	/**
	 * 재입찰번호 (예: 000=최초입찰, 001=재입찰)
	 */
	private String rbidNo;

	/**
	 * 입찰공고명
	 */
	private String bidNtceNm;

	/**
	 * 예정가격 (원, 최종 확정된 예정가격)
	 */
	private BigDecimal plnprc;

	/**
	 * 기초금액 (원, 예정가격 산출의 기초가 되는 금액)
	 */
	private BigDecimal bssamt;

	/**
	 * 총예비가격개수 (생성된 전체 예비가격 수)
	 */
	private Integer totRsrvtnPrceNum;

	/**
	 * 작성번호예비가격일련번호 (예비가격 작성 일련번호)
	 */
	private String compnoRsrvtnPrceSno;

	/**
	 * 기초예정가격 (원, 기초가 되는 예정가격)
	 */
	private BigDecimal bsisPlnprc;

	/**
	 * 추첨여부 (Y:추첨함, N:추첨안함)
	 */
	private String drwtYn;

	/**
	 * 추첨번호 (예비가격 중 추첨된 번호)
	 */
	private String drwtNum;

	/**
	 * 낙찰자선정신청기준내용 (낙찰자 선정 시 적용되는 기준)
	 */
	private String bidwinrSlctnAplBssCntnts;

	/**
	 * 실제개찰일시 (실제로 개찰이 진행된 일시)
	 */
	private String rlOpengDt;

	/**
	 * 작성번호예비가격작성일시 (예비가격이 작성된 일시)
	 */
	private String compnoRsrvtnPrceMkngDt;

	/**
	 * 입력일시 (시스템에 입력된 일시)
	 */
	private String inptDt;

	/**
	 * 기초금액기초상향번호 (기초금액 상향 조정 번호)
	 */
	private String bssamtBssUpNum;

	/**
	 * 예정가격순공사비 (원, 건설공사의 순수 공사비)
	 */
	private BigDecimal prearngPrcePurcnstcst;

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