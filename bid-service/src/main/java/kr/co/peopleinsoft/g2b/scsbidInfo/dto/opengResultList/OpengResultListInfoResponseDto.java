package kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList;

import kr.co.peopleinsoft.cmmn.dto.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 낙찰목록 ResponseDto
 */
@Getter
@Setter
public class OpengResultListInfoResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<OpengResultListInfoDto> response;
}