package kr.co.peopleinsoft.g2b.bidPublicInfo.dto;

import kr.co.peopleinsoft.cmmn.dto.BidCmmnDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BidPublicInfoDto extends BidCmmnDto implements Serializable {

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
	 * 재공고여부 (Y:재공고, N:최초공고)
	 */
	private String reNtceYn;

	/**
	 * 등록유형명 (예: 조달청 또는 나라장터 자체 공고건)
	 */
	private String rgstTyNm;

	/**
	 * 공고종류명 (예: 등록공고, 지명경쟁입찰공고 등)
	 */
	private String ntceKindNm;

	/**
	 * 국제입찰여부 (Y:국제입찰, N:국내입찰)
	 */
	private String intrbidYn;

	/**
	 * 입찰공고일시
	 */
	private String bidNtceDt;

	/**
	 * 참조번호 (발주기관 내부 관리번호)
	 */
	private String refNo;

	/**
	 * 입찰공고명
	 */
	private String bidNtceNm;

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
	 * 입찰방식명 (예: 전자입찰, 직찰 등)
	 */
	private String bidMethdNm;

	/**
	 * 계약체결방법명 (예: 제한경쟁, 일반경쟁 등)
	 */
	private String cntrctCnclsMthdNm;

	/**
	 * 공고기관담당자명
	 */
	private String ntceInsttOfclNm;

	/**
	 * 공고기관담당자전화번호
	 */
	private String ntceInsttOfclTelNo;

	/**
	 * 공고기관담당자이메일주소
	 */
	private String ntceInsttOfclEmailAdrs;

	/**
	 * 집행담당자명
	 */
	private String exctvNm;

	/**
	 * 수요기관담당자이메일주소
	 */
	private String dminsttOfclEmailAdrs;

	/**
	 * 입찰참가자격등록일시
	 */
	private String bidQlfctRgstDt;

	/**
	 * 공동수급협정서접수방법
	 */
	private String cmmnSpldmdAgrmntRcptdocMethd;

	/**
	 * 공동수급협정서마감일시
	 */
	private String cmmnSpldmdAgrmntClseDt;

	/**
	 * 공동수급체지역제한여부 (Y:제한, N:무제한)
	 */
	private String cmmnSpldmdCorpRgnLmtYn;

	/**
	 * 입찰시작일시
	 */
	private String bidBeginDt;

	/**
	 * 입찰마감일시
	 */
	private String bidClseDt;

	/**
	 * 개찰일시
	 */
	private String opengDt;

	/**
	 * 재입찰개찰일시
	 */
	private String rbidOpengDt;

	/**
	 * 입찰보증서접수마감일시
	 */
	private String bidWgrnteeRcptClseDt;

	/**
	 * 공고규격문서URL1
	 */
	private String ntceSpecDocUrl1;

	/**
	 * 공고규격문서URL2
	 */
	private String ntceSpecDocUrl2;

	/**
	 * 공고규격문서URL3
	 */
	private String ntceSpecDocUrl3;

	/**
	 * 공고규격문서URL4
	 */
	private String ntceSpecDocUrl4;

	/**
	 * 공고규격문서URL5
	 */
	private String ntceSpecDocUrl5;

	/**
	 * 공고규격문서URL6
	 */
	private String ntceSpecDocUrl6;

	/**
	 * 공고규격문서URL7
	 */
	private String ntceSpecDocUrl7;

	/**
	 * 공고규격문서URL8
	 */
	private String ntceSpecDocUrl8;

	/**
	 * 공고규격문서URL9
	 */
	private String ntceSpecDocUrl9;

	/**
	 * 공고규격문서URL10
	 */
	private String ntceSpecDocUrl10;

	/**
	 * 공고규격파일명1
	 */
	private String ntceSpecFileNm1;

	/**
	 * 공고규격파일명2
	 */
	private String ntceSpecFileNm2;

	/**
	 * 공고규격파일명3
	 */
	private String ntceSpecFileNm3;

	/**
	 * 공고규격파일명4
	 */
	private String ntceSpecFileNm4;

	/**
	 * 공고규격파일명5
	 */
	private String ntceSpecFileNm5;

	/**
	 * 공고규격파일명6
	 */
	private String ntceSpecFileNm6;

	/**
	 * 공고규격파일명7
	 */
	private String ntceSpecFileNm7;

	/**
	 * 공고규격파일명8
	 */
	private String ntceSpecFileNm8;

	/**
	 * 공고규격파일명9
	 */
	private String ntceSpecFileNm9;

	/**
	 * 공고규격파일명10
	 */
	private String ntceSpecFileNm10;

	/**
	 * 재입찰허용여부 (Y:허용, N:불허)
	 */
	private String rbidPermsnYn;

	/**
	 * 사전심사신청서접수방법명
	 */
	private String pqApplDocRcptMthdNm;

	/**
	 * 사전심사신청서접수일시
	 */
	private String pqApplDocRcptDt;

	/**
	 * 기술평가신청방법명
	 */
	private String tpEvalApplMthdNm;

	/**
	 * 기술평가신청마감일시
	 */
	private String tpEvalApplClseDt;

	/**
	 * 실적신청서접수방법명
	 */
	private String arsltApplDocRcptMthdNm;

	/**
	 * 실적신청서접수일시
	 */
	private String arsltApplDocRcptDt;

	/**
	 * 실적요청서접수일시
	 */
	private String arsltReqstdocRcptDt;

	/**
	 * 공동계약의무지역명1
	 */
	private String jntcontrctDutyRgnNm1;

	/**
	 * 공동계약의무지역명2
	 */
	private String jntcontrctDutyRgnNm2;

	/**
	 * 공동계약의무지역명3
	 */
	private String jntcontrctDutyRgnNm3;

	/**
	 * 지역의무공동계약비율 (%)
	 */
	private String rgnDutyJntcontrctRt;

	/**
	 * 지역의무공동계약여부 (Y:의무, N:비의무)
	 */
	private String rgnDutyJntcontrctYn;

	/**
	 * 지역제한입찰지역판단기준코드
	 */
	private String rgnLmtBidLocplcJdgmBssCd;

	/**
	 * 지역제한입찰지역판단기준명
	 */
	private String rgnLmtBidLocplcJdgmBssNm;

	/**
	 * 건설현장지역명 (건설공사)
	 */
	private String cnstrsiteRgnNm;

	/**
	 * 인센티브지역명1 (건설공사)
	 */
	private String incntvRgnNm1;

	/**
	 * 인센티브지역명2 (건설공사)
	 */
	private String incntvRgnNm2;

	/**
	 * 인센티브지역명3 (건설공사)
	 */
	private String incntvRgnNm3;

	/**
	 * 인센티브지역명4 (건설공사)
	 */
	private String incntvRgnNm4;

	/**
	 * 세부입찰여부 (Y:세부입찰, N:일반입찰)
	 */
	private String dtlsBidYn;

	/**
	 * 입찰참가제한여부 (Y:제한있음, N:제한없음)
	 */
	private String bidPrtcptLmtYn;

	/**
	 * 업종제한여부 (Y:제한있음, N:제한없음)
	 */
	private String indstrytyLmtYn;

	/**
	 * 업종제조분야평가여부 (Y:평가함, N:평가안함)
	 */
	private String indstrytyMfrcFldEvlYn;

	/**
	 * 예정가격결정방법명 (예: 복수예가, 단일예가 등)
	 */
	private String prearngPrceDcsnMthdNm;

	/**
	 * 총예가개수 (예정가격 작성 시 생성되는 전체 예가 수)
	 */
	private String totPrdprcNum;

	/**
	 * 추첨예가개수 (실제 추첨하는 예가 수)
	 */
	private Integer drwtPrdprcNum;

	/**
	 * 예산금액 (원)
	 */
	private BigDecimal bdgtAmt;

	/**
	 * 배정예산금액 (원)
	 */
	private BigDecimal asignBdgtAmt;

	/**
	 * 추정가격 (원)
	 */
	private BigDecimal presmtPrce;

	/**
	 * 관급자재금액 (원)
	 */
	private BigDecimal govsplyAmt;

	/**
	 * 계약건설관급자재금액 (원, 건설공사)
	 */
	private BigDecimal contrctrcnstrtnGovsplyMtrlAmt;

	/**
	 * 국가건설관급자재금액 (원, 건설공사)
	 */
	private BigDecimal govcnstrtnGovsplyMtrlAmt;

	/**
	 * 주공종명 (건설공사)
	 */
	private String mainCnsttyNm;

	/**
	 * 주공종공사예정금액 (원, 건설공사)
	 */
	private BigDecimal mainCnsttyCnstwkPrearngAmt;

	/**
	 * 주공종추정가격 (건설공사)
	 */
	private String mainCnsttyPresmtPrce;

	/**
	 * 신청기준내용 (건설공사)
	 */
	private String aplBssCntnts;

	/**
	 * 업종평가비율 (%)
	 */
	private String indstrytyEvlRt;

	/**
	 * 공종분담비율목록 (건설공사)
	 */
	private String cnsttyAccotShreRateList;

	/**
	 * 건설능력평가금액목록 (건설공사)
	 */
	private String cnstrtnAbltyEvlAmtList;

	/**
	 * 하위공종명1 (건설공사)
	 */
	private String subsiCnsttyNm1;

	/**
	 * 하위공종명2 (건설공사)
	 */
	private String subsiCnsttyNm2;

	/**
	 * 하위공종명3 (건설공사)
	 */
	private String subsiCnsttyNm3;

	/**
	 * 하위공종명4 (건설공사)
	 */
	private String subsiCnsttyNm4;

	/**
	 * 하위공종명5 (건설공사)
	 */
	private String subsiCnsttyNm5;

	/**
	 * 하위공종명6 (건설공사)
	 */
	private String subsiCnsttyNm6;

	/**
	 * 하위공종명7 (건설공사)
	 */
	private String subsiCnsttyNm7;

	/**
	 * 하위공종명8 (건설공사)
	 */
	private String subsiCnsttyNm8;

	/**
	 * 하위공종명9 (건설공사)
	 */
	private String subsiCnsttyNm9;

	/**
	 * 하위공종업종평가비율1 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt1;

	/**
	 * 하위공종업종평가비율2 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt2;

	/**
	 * 하위공종업종평가비율3 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt3;

	/**
	 * 하위공종업종평가비율4 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt4;

	/**
	 * 하위공종업종평가비율5 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt5;

	/**
	 * 하위공종업종평가비율6 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt6;

	/**
	 * 하위공종업종평가비율7 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt7;

	/**
	 * 하위공종업종평가비율8 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt8;

	/**
	 * 하위공종업종평가비율9 (%, 건설공사)
	 */
	private String subsiCnsttyIndstrytyEvlRt9;

	/**
	 * 개찰장소
	 */
	private String opengPlce;

	/**
	 * 서류개찰일시
	 */
	private String dcmtgOprtnDt;

	/**
	 * 서류개찰장소
	 */
	private String dcmtgOprtnPlce;

	/**
	 * 입찰공고상세URL (나라장터 상세페이지)
	 */
	private String bidNtceDtlUrl;

	/**
	 * 입찰공고URL
	 */
	private String bidNtceUrl;

	/**
	 * 표준공고문서URL
	 */
	private String stdNtceDocUrl;

	/**
	 * 특별설명문서URL1 (건설공사)
	 */
	private String sptDscrptDocUrl1;

	/**
	 * 특별설명문서URL2 (건설공사)
	 */
	private String sptDscrptDocUrl2;

	/**
	 * 특별설명문서URL3 (건설공사)
	 */
	private String sptDscrptDocUrl3;

	/**
	 * 특별설명문서URL4 (건설공사)
	 */
	private String sptDscrptDocUrl4;

	/**
	 * 특별설명문서URL5 (건설공사)
	 */
	private String sptDscrptDocUrl5;

	/**
	 * 입찰참가비납부여부 (Y:납부, N:면제)
	 */
	private String bidPrtcptFeePaymntYn;

	/**
	 * 입찰참가비 (원)
	 */
	private String bidPrtcptFee;

	/**
	 * 입찰보증금납부여부 (Y:납부, N:면제)
	 */
	private String bidGrntymnyPaymntYn;

	/**
	 * 채권자명 (계약 체결 상대방)
	 */
	private String crdtrNm;

	/**
	 * 통합공고번호
	 */
	private String untyNtceNo;

	/**
	 * 공동수급방법코드
	 */
	private String cmmnSpldmdMethdCd;

	/**
	 * 공동수급방법명 (예: 공동수급허용, 공동수급불허)
	 */
	private String cmmnSpldmdMethdNm;

	/**
	 * 공동수급체수 (공동수급 가능 업체 수)
	 */
	private String cmmnSpldmdCnum;

	/**
	 * 저가입찰가격허용여부 (Y:허용, N:불허)
	 */
	private String brffcBidprcPermsnYn;

	/**
	 * 지명경쟁여부 (Y:지명경쟁, N:일반경쟁)
	 */
	private String dsgntCmptYn;

	/**
	 * 실적경쟁여부 (Y:실적경쟁, N:일반입찰)
	 */
	private String arsltCmptYn;

	/**
	 * 사전심사여부 (Y:사전심사, N:사후심사)
	 */
	private String pqEvalYn;

	/**
	 * 기술평가여부 (Y:기술평가, N:일반입찰)
	 */
	private String tpEvalYn;

	/**
	 * 공고설명여부 (Y:설명회실시, N:미실시)
	 */
	private String ntceDscrptYn;

	/**
	 * 예비가격재작성방법명
	 */
	private String rsrvtnPrceReMkngMthdNm;

	/**
	 * 발주계획통합번호
	 */
	private String orderPlanUntyNo;

	/**
	 * 낙찰하한율 (%)
	 */
	private String sucsfbidLwltRate;

	/**
	 * 낙찰방법코드
	 */
	private String sucsfbidMthdCd;

	/**
	 * 낙찰방법명 (예: 최저가낙찰제, 적격심사, 협상에 의한 계약 등)
	 */
	private String sucsfbidMthdNm;

	/**
	 * 시공책임형CM적용여부 (Y:적용, N:미적용, 건설공사)
	 */
	private String ciblAplYn;

	/**
	 * 복수업종사전등록가능여부 (Y:가능, N:불가능, 건설공사)
	 */
	private String mtltyAdvcPsblYn;

	/**
	 * 복수업종사전등록가능여부공종명 (건설공사)
	 */
	private String mtltyAdvcPsblYnCnstwkNm;

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

	/**
	 * 변경공고사유
	 */
	private String chgNtceRsn;

	/**
	 * 이전규격등록번호
	 */
	private String bfSpecRgstNo;

	/**
	 * 부가가치세
	 */
	private String vat;

	/**
	 * 개별소비세
	 */
	private String indutyVat;

	/**
	 * 공공서비스용역여부 (Y:공공서비스용역, N:일반용역)
	 */
	private String ppswGnrlSrvceYn;

	/**
	 * 용역구분명 (예: 일반용역, 전문용역 등)
	 */
	private String srvceDivNm;

	/**
	 * 제품분류제한여부 (Y:제한있음, N:제한없음, 물품구매)
	 */
	private String prdctClsfcLmtYn;

	/**
	 * 제조여부 (Y:제조품, N:기성품, 물품구매)
	 */
	private String mnfctYn;

	/**
	 * 구매대상물품목록 (물품구매)
	 */
	private String purchsObjPrdctList;

	/**
	 * 정보화사업여부 (Y:정보화사업, N:일반사업)
	 */
	private String infoBizYn;

	/**
	 * 공공조달대분류명
	 */
	private String pubPrcrmntLrgClsfcNm;

	/**
	 * 공공조달중분류명
	 */
	private String pubPrcrmntMidClsfcNm;

	/**
	 * 공공조달분류번호
	 */
	private String pubPrcrmntClsfcNo;

	/**
	 * 공공조달분류명
	 */
	private String pubPrcrmntClsfcNm;

	/**
	 * 기술능력평가비율 (%)
	 */
	private String techAbltEvlRt;

	/**
	 * 입찰가격평가비율 (%)
	 */
	private String bidPrceEvlRt;

	/**
	 * 제품일련번호 (물품구매)
	 */
	private String prdctSno;

	/**
	 * 상세제품분류번호 (물품구매)
	 */
	private String dtilPrdctClsfcNo;

	/**
	 * 상세제품분류번호명 (물품구매)
	 */
	private String dtilPrdctClsfcNoNm;

	/**
	 * 제품규격명 (물품구매)
	 */
	private String prdctSpecNm;

	/**
	 * 제품수량 (물품구매)
	 */
	private BigDecimal prdctQty;

	/**
	 * 제품단위 (예: EA, BOX, SET 등, 물품구매)
	 */
	private String prdctUnit;

	/**
	 * 제품단가 (원, 물품구매)
	 */
	private BigDecimal prdctUprc;

	/**
	 * 납품기한일시 (물품구매)
	 */
	private String dlvrTmlmtDt;

	/**
	 * 납품일수 (계약체결 후 납품까지 소요일수, 물품구매)
	 */
	private Integer dlvrDaynum;

	/**
	 * 납품조건명 (예: 본사납품, 지정장소납품 등, 물품구매)
	 */
	private String dlvryCndtnNm;

	/**
	 * 입찰참가자격등록내용(기타)
	 */
	private String bidQlfctRgstCntnts;

	/**
	 * 입찰보증금납부대상여부(기타)
	 */
	private String bidGrntymnyPaymntObjYn;

	/**
	 * 추정가격(기타)
	 */
	private BigDecimal presmptPrce;

	/**
	 * 비고내용(기타)
	 */
	private String rmrkCntnts;

	/**
	 * 공고유형
	 */
	private String bidType;

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