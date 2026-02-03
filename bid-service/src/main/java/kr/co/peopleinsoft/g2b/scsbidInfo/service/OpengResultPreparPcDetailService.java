package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultPreparPcDetail.OpengResultPreparPcDetailDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class OpengResultPreparPcDetailService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertOpengResultPreparPcDetailInfo(URI uri, int pageNo, List<OpengResultPreparPcDetailDto> items, T requestDto) {
		int rowCnt;

		for (OpengResultPreparPcDetailDto item : items) {
			cmmnMapper.insert("OpengResultListInfoPreparPcDetailMapper.batchInsertOpengResultListInfoPreparPcDetail", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}