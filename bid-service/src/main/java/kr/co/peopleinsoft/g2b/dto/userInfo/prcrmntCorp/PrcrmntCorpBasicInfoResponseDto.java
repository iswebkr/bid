package kr.co.peopleinsoft.g2b.dto.userInfo.prcrmntCorp;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PrcrmntCorpBasicInfoResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<PrcrmntCorpBasicInfoDto> response;
}