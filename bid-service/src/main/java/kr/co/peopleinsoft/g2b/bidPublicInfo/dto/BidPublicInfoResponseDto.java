package kr.co.peopleinsoft.g2b.bidPublicInfo.dto;

import kr.co.peopleinsoft.cmmn.dto.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BidPublicInfoResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<BidPublicInfoDto> response;
}