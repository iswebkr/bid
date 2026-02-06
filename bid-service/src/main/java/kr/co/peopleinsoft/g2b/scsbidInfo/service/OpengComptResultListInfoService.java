package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengComptList.OpengComptResultListInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class OpengComptResultListInfoService extends G2BAbstractBidService {
	public int batchInsertOpengResultListInfo(List<OpengComptResultListInfoDto> items) {
		int rowCnt;
		for (OpengComptResultListInfoDto item : items) {
			cmmnMapper.insert("OpengComptResultListInfoMapper.batchInsertOpengComptResultListInfo", item);
		}
		rowCnt = cmmnMapper.flushBatchStatementsCount();
		return rowCnt;
	}

	public <T extends BidRequestDto> int batchInsertOpengResultListInfo(URI uri, int pageNo, List<OpengComptResultListInfoDto> items, T requestDto) {
		int rowCnt = batchInsertOpengResultListInfo(items);
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);
		return rowCnt;
	}
}