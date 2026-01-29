package kr.co.peopleinsoft.mois.stanOrgCd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
public class StanOrgCdRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String serviceKey; // 인증키
	private String serviceId;
	private int pageNo; // 페이지번호
	private int numOfRows; // 한페이지결과서
	private String type; // 호출문서 (xml, json)
	private String fullNm; // 기관명
	private String orgCd; // 기관코드
	private String stopSelt; // 사용 :0, 폐지 : 1

}