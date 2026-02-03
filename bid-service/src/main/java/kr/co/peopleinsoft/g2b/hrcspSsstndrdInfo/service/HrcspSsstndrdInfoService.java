package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service;


import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class HrcspSsstndrdInfoService extends G2BAbstractBidService {
	public <T1 extends BidRequestDto> int batchInsertHrcspSsstndrdInfo(URI uri, int pageNo, List<HrcspSsstndrdInfoDto> items, T1 requestDto) {
		int rowCnt;

		for (HrcspSsstndrdInfoDto item : items) {
			cmmnMapper.insert("BidHrcspSsstndrdInfoMapper.batchInsertHrcspSsstndrdInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}