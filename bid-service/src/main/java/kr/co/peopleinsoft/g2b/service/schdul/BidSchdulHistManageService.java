package kr.co.peopleinsoft.g2b.service.schdul;

import kr.co.peopleinsoft.biz.service.CmmnAbstractService;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidColctHistDto;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidColctHistResultDto;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BidSchdulHistManageService extends CmmnAbstractService {

	private final Logger logger = LoggerFactory.getLogger(BidSchdulHistManageService.class);

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

	public <T extends BidRequestDto> boolean colctCmplYn(T requestDto) {
		BidColctHistDto paramDto = BidColctHistDto.builder()
			.colctId(requestDto.getServiceId())
			.colctBgnDt(requestDto.getInqryBgnDt())
			.colctEndDt(requestDto.getInqryEndDt())
			.build();

		BidColctHistResultDto resultDto = cmmnMapper.selectOne("BidColctHistMapper.selectColctHistResultByDate", paramDto);

		if (resultDto == null) {
			return false;
		}
		return "수집완료".equals(resultDto.getColctState());
	}
}