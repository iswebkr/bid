package kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList;

import kr.co.peopleinsoft.cmmn.dto.BidResponse;
import kr.co.peopleinsoft.cmmn.dto.BidResponseDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 낙찰목록 ResponseDto
 */
@Getter
@Setter
public class OpengResultListInfoResponseDto extends BidResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<OpengResultListInfoDto> response;

	public List<OpengResultListInfoDto> getItems() {
		if (response != null) {
			return response.getBody().getItems();
		}
		return new ArrayList<>();
	}

	public int getTotalCount() {
		return response.getBody().getTotalCount();
	}

	public int getTotalPage() {
		return (int) Math.ceil((double) response.getBody().getTotalCount() / 100);
	}
}