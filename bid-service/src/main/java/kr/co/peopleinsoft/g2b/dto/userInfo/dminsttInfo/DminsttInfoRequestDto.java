package kr.co.peopleinsoft.g2b.dto.userInfo.dminsttInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class DminsttInfoRequestDto extends BidRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String orderBgnYm; // 조회기준시작일시 (yyyyMMddHHmm)
	private String orderEndYm; // 조회기준종료일시 (yyyyMMddHHmm)
	private String dminsttCd; // 수요기관코드
	private String dminsttNm; // 수요기관명
	private String bizno; // 사업자등록번호
}