package kr.co.peopleinsoft.g2b.dto.hrcspSsstndrdInfo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class HrcspSsstndrdInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	// ========================================
	// 기본 정보
	// ========================================

	/**
	 * 사전규격등록번호 (사전규격의 고유 등록번호)
	 */
	private String bfSpecRgstNo;

	/**
	 * 참조번호 (참조용 번호)
	 */
	private String refNo;

	/**
	 * 업무구분명 (예: 물품, 용역, 공사 등)
	 */
	private String bsnsDivNm;

	/**
	 * 제품분류번호명 (제품 분류의 명칭)
	 */
	private String prdctClsfcNoNm;

	/**
	 * 제품상세목록 (제품 상세 정보 목록, JSON 형식 권장)
	 */
	private String prdctDtlList;

	/**
	 * 발주기관명 (발주를 담당하는 기관명)
	 */
	private String orderInsttNm;

	/**
	 * 실수요기관명 (실제 물품을 사용할 수요기관명)
	 */
	private String rlDminsttNm;

	/**
	 * 배정예산금액 (원, 배정된 예산 금액)
	 */
	private BigDecimal asignBdgtAmt;

	/**
	 * 접수일자 (사전규격이 접수된 날짜)
	 */
	private String rcptDt;

	/**
	 * 개찰등록마감일시 (개찰 등록이 마감되는 일시)
	 */
	private String opninRgstClseDt;

	/**
	 * 납품기한일자 (납품 완료 기한)
	 */
	private String dlvrTmlmtDt;

	/**
	 * 납품일수 (계약 후 납품까지 소요 일수)
	 */
	private Integer dlvrDaynum;

	/**
	 * 담당자명 (사전규격 담당자 성명)
	 */
	private String ofclNm;

	/**
	 * 담당자전화번호 (담당자 연락처)
	 */
	private String ofclTelNo;

	/**
	 * 소프트웨어사업대상여부 (Y:소프트웨어사업, N:일반사업)
	 */
	private String swBizObjYn;

	/**
	 * 규격서파일URL1 (규격서 파일 다운로드 URL1)
	 */
	private String specDocFileUrl1;

	/**
	 * 규격서파일URL2 (규격서 파일 다운로드 URL2)
	 */
	private String specDocFileUrl2;

	/**
	 * 규격서파일URL3 (규격서 파일 다운로드 URL3)
	 */
	private String specDocFileUrl3;

	/**
	 * 규격서파일URL4 (규격서 파일 다운로드 URL4)
	 */
	private String specDocFileUrl4;

	/**
	 * 규격서파일URL5 (규격서 파일 다운로드 URL5)
	 */
	private String specDocFileUrl5;

	/**
	 * 등록일시 (조달청 시스템 등록일시)
	 */
	private String rgstDt;

	/**
	 * 변경일시 (조달청 시스템 변경일시)
	 */
	private String chgDt;

	/**
	 * 입찰공고번호목록 (연관된 입찰공고번호 목록, JSON 또는 구분자로 저장)
	 */
	private String bidNtceNoList;

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