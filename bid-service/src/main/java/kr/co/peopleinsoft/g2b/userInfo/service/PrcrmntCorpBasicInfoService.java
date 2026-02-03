package kr.co.peopleinsoft.g2b.userInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class PrcrmntCorpBasicInfoService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertPrcrmntCorpBasicInfo(URI uri, int pageNo, List<PrcrmntCorpBasicInfoDto> items, T requestDto) {
		int rowCnt;

		for (PrcrmntCorpBasicInfoDto item : items) {
			cmmnMapper.insert("PrcrmntCorpBasicInfoMapper.batchInsertPrcrmntCorpBasicInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}