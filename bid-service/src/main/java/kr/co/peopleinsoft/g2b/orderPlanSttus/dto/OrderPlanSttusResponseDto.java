package kr.co.peopleinsoft.g2b.orderPlanSttus.dto;

import kr.co.peopleinsoft.cmmn.dto.BidResponse;
import kr.co.peopleinsoft.cmmn.dto.BidResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderPlanSttusResponseDto extends BidResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private BidResponse<OrderPlanSttusDto> response;

	public List<OrderPlanSttusDto> getItems() {
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