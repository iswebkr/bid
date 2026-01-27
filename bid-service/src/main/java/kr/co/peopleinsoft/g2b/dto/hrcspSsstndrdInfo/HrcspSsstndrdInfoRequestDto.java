package kr.co.peopleinsoft.g2b.dto.hrcspSsstndrdInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class HrcspSsstndrdInfoRequestDto extends BidRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String bfSpecRgstNo; // 사전규격등록번호
	private String orderInsttNm; // 발주기관명
	private String rlDminsttNm; // 실수요기관명

	// 품목
	private String prdctClsfcNoNm; // 품명
	private String dtilPrdctClsfcNo; // 세부품명번호
	private String dtilPrdctClsfcNoNm; // 세부품명

	private String refNo; // 참조번호
	private String ntceInsttCd; // 공고기관코드
	private String ntceInsttNm; // 공고기관명
	private String dminsttCd; // 수요기관코드
	private String dminsttNm; // 수요기관명
	private String swBizObjYn; // SW사업대상여부
}