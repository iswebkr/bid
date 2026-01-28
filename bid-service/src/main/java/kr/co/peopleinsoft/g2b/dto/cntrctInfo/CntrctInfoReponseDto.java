package kr.co.peopleinsoft.g2b.dto.cntrctInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class CntrctInfoReponseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<CntrctInfoDto> response;
}