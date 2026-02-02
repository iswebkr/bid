package kr.co.peopleinsoft.cmmn.service;

import kr.co.peopleinsoft.biz.service.CmmnAbstractService;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistResultDto;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BidSchdulHistManageService extends CmmnAbstractService {

	private final Logger logger = LoggerFactory.getLogger(BidSchdulHistManageService.class);

	public <T extends BidRequestDto> int getStartPage(T requestDto) {
		int startPage = 1;
		BidColctHistDto paramDto = BidColctHistDto.builder()
			.colctId(requestDto.getServiceId())
			.colctBgnDt(requestDto.getInqryBgnDt())
			.colctEndDt(requestDto.getInqryEndDt())
			.build();
		BidColctHistResultDto resultDto = cmmnMapper.selectOne("BidColctHistMapper.selectColctHistResultByDate", paramDto);
		if (resultDto != null) {
			startPage = resultDto.getCmplColctPage();
		}
		return startPage;
	}

	public <T extends BidRequestDto> BidColctHistResultDto selectBidColctHistResultDto(T requestDto) {
		BidColctHistDto paramDto = BidColctHistDto.builder()
			.colctId(requestDto.getServiceId())
			.colctBgnDt(requestDto.getInqryBgnDt())
			.colctEndDt(requestDto.getInqryEndDt())
			.build();
		return cmmnMapper.selectOne("BidColctHistMapper.selectColctHistResultByDate", paramDto);
	}

	public void updateColctPageInfo(BidColctHistDto paramDto) {
		cmmnMapper.update("BidColctHistMapper.updateColctPageInfo", paramDto);
	}
}