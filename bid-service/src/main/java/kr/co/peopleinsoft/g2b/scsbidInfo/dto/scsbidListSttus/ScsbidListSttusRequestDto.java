package kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 낙찰목록 RequestDto
 */
@Getter
@Setter
@SuperBuilder
public class ScsbidListSttusRequestDto extends BidRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
}