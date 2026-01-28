package kr.co.peopleinsoft.g2b.dto.cntrctInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
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