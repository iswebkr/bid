package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto;

import kr.co.peopleinsoft.cmmn.dto.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class HrcspSsstndrdInfoResponseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<HrcspSsstndrdInfoDto> response;
}