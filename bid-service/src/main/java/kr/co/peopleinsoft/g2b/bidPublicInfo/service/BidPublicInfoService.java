package kr.co.peopleinsoft.g2b.bidPublicInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class BidPublicInfoService extends G2BAbstractBidService {
	public int batchInsertPublicInfo(List<BidPublicInfoDto> items) {
		int rowCnt;
		for (BidPublicInfoDto item : items) {
			cmmnMapper.insert("BidPublicInfoMapper.batchInsertPublicInfo", item);
		}
		rowCnt = cmmnMapper.flushBatchStatementsCount();
		return rowCnt;
	}

	public <T extends BidRequestDto> int batchInsertPublicInfo(URI uri, int pageNo, List<BidPublicInfoDto> items, T requestDto) {
		int rowCnt = batchInsertPublicInfo(items);
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);
		return rowCnt;
	}
}