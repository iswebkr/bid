package kr.co.peopleinsoft.g2b.dto.orderPlanSttus;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class OrderPlanSttusRequestDto extends BidRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String orderBgnYm; // 발주시작년월
	private String orderEndYm; // 발주종료년월
	private String orderPlanUntyNo; // 발주계획통합번호
	private String orderInsttCd; // 발주기관코드
	private String orderInsttNm; // 발주기관명

	private String agrmntYn; // 협정여부
	private String prcrmntMethd; // 조달방식
	private String insttLctNm; // 기관소재지명
	private String dtilPrdctClsfcNo; // 세부품명번호
	private String bizNm; // 사업명
	private String cnsttyDivNm; // 공종구분명

	private String bsnsTyCd; // 업무유형코드
	private String bsnsTyNm; // 업무유형명
}