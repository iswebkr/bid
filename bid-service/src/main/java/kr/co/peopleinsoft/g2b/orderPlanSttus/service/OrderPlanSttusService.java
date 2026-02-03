package kr.co.peopleinsoft.g2b.orderPlanSttus.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class OrderPlanSttusService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertBidOrderPlan(URI uri, int pageNo, List<OrderPlanSttusDto> items, T requestDto) {
		int rowCnt;

		for (OrderPlanSttusDto item : items) {
			cmmnMapper.insert("BidOrderPlanSttusMapper.batchInsertOrderPlan", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}