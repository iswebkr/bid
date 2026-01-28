package kr.co.peopleinsoft.cmmn.service;

import kr.co.peopleinsoft.biz.service.CmmnAbstractService;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;

import java.net.URI;

public class G2BAbstractBidService extends CmmnAbstractService {
	protected <T extends BidRequestDto> void insertSchdulHistLog(URI uri, int pageNo, T requestDto, int totalPage, int totalCount, int rowCnt) {
		// 스케줄러 로그 기록
		BidColctHistDto bidSchdulHistManageDto = BidColctHistDto.builder()
			.colctId(requestDto.getServiceId())
			.colctUri(uri.toString())
			.colctDesc(requestDto.getServiceDescription())
			.colctBgnDt(requestDto.getInqryBgnDt())
			.colctEndDt(requestDto.getInqryEndDt())
			.colctTotPage(totalPage)
			.colctCmplPage(pageNo)
			.colctTotCnt(totalCount)
			.colctCmplCnt(rowCnt)
			.build();
		cmmnMapper.insert("BidColctHistMapper.batchInsertColctHist", bidSchdulHistManageDto);
	}
}