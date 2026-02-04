package kr.co.peopleinsoft.cmmn.service;

import kr.co.peopleinsoft.biz.service.CmmnAbstractService;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistResultDto;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import org.springframework.stereotype.Service;

@Service
public class BidSchdulHistManageService extends CmmnAbstractService {
	public <T extends BidRequestDto> int getStartPage(T requestDto) {
		int startPage = 1;

		BidColctHistDto paramDto = new BidColctHistDto();
		paramDto.setColctId(requestDto.getServiceId());
		paramDto.setColctBgnDt(requestDto.getInqryBgnDt());
		paramDto.setColctEndDt(requestDto.getInqryEndDt());

		BidColctHistResultDto resultDto = cmmnMapper.selectOne("BidColctHistMapper.selectColctHistResultByDate", paramDto);

		if(resultDto == null) {
			return startPage;
		}

		startPage = resultDto.getColctCmplPage();

		if ("complete".equals(resultDto.getColctState())) {
			startPage = resultDto.getColctCmplPage() + 1;
		}

		return startPage;
	}

	public <T extends BidRequestDto> BidColctHistResultDto selectBidColctHistResultDto(T requestDto) {

		BidColctHistDto paramDto = new BidColctHistDto();
		paramDto.setColctId(requestDto.getServiceId());
		paramDto.setColctBgnDt(requestDto.getInqryBgnDt());
		paramDto.setColctEndDt(requestDto.getInqryEndDt());
		return cmmnMapper.selectOne("BidColctHistMapper.selectColctHistResultByDate", paramDto);
	}

	public void updateColctPageInfo(BidColctHistDto paramDto) {
		cmmnMapper.update("BidColctHistMapper.updateColctPageInfo", paramDto);
	}
}