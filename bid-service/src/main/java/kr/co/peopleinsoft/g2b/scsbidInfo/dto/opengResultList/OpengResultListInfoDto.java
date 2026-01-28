package kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class OpengResultListInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

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
	 * 개찰일시 (개찰이 진행된 일시)
	 */
	private String opengDt;

	/**
	 * 참가업체수 (개찰에 참가한 총 업체 수)
	 */
	private Integer prtcptCnum;

	/**
	 * 개찰업체정보 (참가 업체들의 상세 정보, TEXT 타입)
	 */
	private String opengCorpInfo;

	/**
	 * 진행구분코드명 (예: 개찰완료, 진행중, 유찰 등)
	 */
	private String progrssDivCdNm;

	/**
	 * 입력일시 (시스템에 입력된 일시)
	 */
	private String inptDt;

	/**
	 * 예비가격파일존재여부 (Y:존재, N:없음)
	 */
	private String rsrvtnPrceFileExistnceYn;

	/**
	 * 공고기관코드
	 */
	private String ntceInsttCd;

	/**
	 * 공고기관명
	 */
	private String ntceInsttNm;

	/**
	 * 수요기관코드
	 */
	private String dminsttCd;

	/**
	 * 수요기관명
	 */
	private String dminsttNm;

	/**
	 * 개찰결과공고내용 (개찰 결과에 대한 상세 설명 또는 공고 내용, TEXT 타입)
	 */
	private String opengRsltNtcCntnts;

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

	/**
	 * 입찰공고번호
	 */
	private String bidNtceNo;

	/**
	 * 입찰공고차수
	 */
	private String bidNtceOrd;

}