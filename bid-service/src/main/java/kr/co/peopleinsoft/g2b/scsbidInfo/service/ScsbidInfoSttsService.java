package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class ScsbidInfoSttsService extends G2BAbstractBidService {
	public int batchInsertScsbidListSttus(List<ScsbidListSttusDto> items) {
		int rowCnt;
		for (ScsbidListSttusDto item : items) {
			cmmnMapper.insert("ScsbidListSttusMapper.batchInsertScsbidListSttus", item);
		}
		rowCnt = cmmnMapper.flushBatchStatementsCount();
		return rowCnt;
	}

	public <T extends BidRequestDto> int batchInsertScsbidListSttus(URI uri, int pageNo, List<ScsbidListSttusDto> items, T requestDto) {
		int rowCnt = batchInsertScsbidListSttus(items);
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);
		return rowCnt;
	}
}