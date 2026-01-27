package kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengComptList;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 낙찰목록 ResponseDto
 */
@Getter
@Setter
public class OpengComptResultListInfoResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<OpengComptResultListInfoDto> response;
}