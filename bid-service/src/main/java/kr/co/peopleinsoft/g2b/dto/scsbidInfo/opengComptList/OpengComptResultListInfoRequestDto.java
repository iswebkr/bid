package kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengComptList;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 개찰완료 RequestDto
 */
@Getter
@Setter
@SuperBuilder
public class OpengComptResultListInfoRequestDto extends BidRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String bidNtceNo; // 입찰공고번호 (입찰공고번호에 해당하는 데이터를 조회해야 함)
}