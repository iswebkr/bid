package kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
public class PrcrmntCorpBasicInfoRequestDto extends BidRequestDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
}