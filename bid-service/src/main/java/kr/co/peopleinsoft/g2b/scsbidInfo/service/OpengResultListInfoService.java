package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList.OpengResultListInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class OpengResultListInfoService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertOpengResultListInfo(URI uri, int pageNo, List<OpengResultListInfoDto> items, T requestDto) {
		int rowCnt;

		for (OpengResultListInfoDto item : items) {
			cmmnMapper.insert("OpengResultListInfoMapper.batchInsertOpengResultListInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}