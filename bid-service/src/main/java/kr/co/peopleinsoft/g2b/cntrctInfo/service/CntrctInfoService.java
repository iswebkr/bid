package kr.co.peopleinsoft.g2b.cntrctInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class CntrctInfoService extends G2BAbstractBidService {
	public <T extends BidRequestDto> int batchInsertHrcspSsstndrdInfo(List<CntrctInfoDto> items) {
		int rowCnt = 0;
		for (CntrctInfoDto item : items) {
			cmmnMapper.insert("CntrctInfoMapper.batchInsertCntrctInfo", item);
			rowCnt++;
		}
		return rowCnt;
	}

	public <T extends BidRequestDto> int batchInsertHrcspSsstndrdInfo(URI uri, int pageNo, List<CntrctInfoDto> items, T requestDto) {
		int rowCnt = 0;
		for (CntrctInfoDto item : items) {
			cmmnMapper.insert("CntrctInfoMapper.batchInsertCntrctInfo", item);
			rowCnt++;
		}
		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);
		return rowCnt;
	}
}