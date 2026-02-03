package kr.co.peopleinsoft.g2b.userInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.userInfo.dto.dminsttInfo.DminsttInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class DminsttInfoService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertDminsttInfo(URI uri, int pageNo, List<DminsttInfoDto> items, T requestDto) {
		int rowCnt;

		for (DminsttInfoDto item : items) {
			cmmnMapper.insert("DminsttInfoMapper.batchInsertDminsttInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}