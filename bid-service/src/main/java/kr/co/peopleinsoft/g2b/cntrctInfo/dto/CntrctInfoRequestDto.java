package kr.co.peopleinsoft.g2b.cntrctInfo.dto;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class CntrctInfoRequestDto extends BidRequestDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
}