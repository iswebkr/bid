package kr.co.peopleinsoft.g2b.orderPlanSttus.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class OrderPlanSttusService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public OrderPlanSttusService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertBidOrderPlan(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		OrderPlanSttusResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(OrderPlanSttusResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<OrderPlanSttusDto> items = responseDto.getResponse().getBody().getItems();

		for (OrderPlanSttusDto item : items) {
			cmmnMapper.insert("BidOrderPlanSttusMapper.batchInsertOrderPlan", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}