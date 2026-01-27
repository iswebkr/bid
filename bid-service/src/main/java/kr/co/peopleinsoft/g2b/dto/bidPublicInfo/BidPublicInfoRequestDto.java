package kr.co.peopleinsoft.g2b.dto.bidPublicInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
public class BidPublicInfoRequestDto extends BidRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String bidNtceNo; // 입찰공고번호
	private String bidNtceNm; // 입찰공고명
	private String bidNtceOrd; // 입찰공고차수
	private String ntcntceInsttCd; // 공고기관코드
	private String ntceInsttNm; // 공고기관명
	private String dminsttCd; // 수요기관코드
	private String dminsttNm; // 수요기관명
	private String refNo; // 참조번호
	private String prtcptLmtRgnCd; // 참가제한지역코드
	private String prtcptLmtRgnNm; // 참가제한지역명
	private String indstrytyCd; // 업종코드
	private String indstrytyNm; // 업종명
	private BigDecimal presmptPrceBgn; // 추정가격시작
	private BigDecimal presmptPrceEnd; // 추정가격종료
	private String prcrmntReqNo; // 조달요청번호
	private String bidClseExcpYn; // 입찰마감제외여부
	private String intrntnlDivCd = "1"; // 국제입찰구분코드

	/*** 물품 ***/
	private String dtilPrdctClsfcNo; // 세부품명번호

	/*** 외자 ***/
	private String masYn; // 다수공급경쟁자여부
}