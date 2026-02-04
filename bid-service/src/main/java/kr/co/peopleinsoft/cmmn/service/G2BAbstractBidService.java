package kr.co.peopleinsoft.cmmn.service;

import kr.co.peopleinsoft.biz.service.CmmnAbstractService;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;

import java.net.URI;

public class G2BAbstractBidService extends CmmnAbstractService {
	protected <T extends BidRequestDto> void insertSchdulHistLog(URI uri, int pageNo, T requestDto, int rowCnt) {
		if (rowCnt > 0) {
			// 스케줄러 로그 기록
			BidColctHistDto bidSchdulHistManageDto = new BidColctHistDto();
			bidSchdulHistManageDto.setColctId(requestDto.getServiceId());
			bidSchdulHistManageDto.setColctUri(uri.toString());
			bidSchdulHistManageDto.setColctDesc(requestDto.getServiceDescription());
			bidSchdulHistManageDto.setColctBgnDt(requestDto.getInqryBgnDt());
			bidSchdulHistManageDto.setColctEndDt(requestDto.getInqryEndDt());
			bidSchdulHistManageDto.setColctTotPage(requestDto.getTotalPage());
			bidSchdulHistManageDto.setColctCmplPage(pageNo);
			bidSchdulHistManageDto.setColctTotCnt(requestDto.getTotalCount());
			bidSchdulHistManageDto.setColctCmplCnt(rowCnt);
			cmmnMapper.insert("BidColctHistMapper.batchInsertColctHist", bidSchdulHistManageDto);
		}
	}
}