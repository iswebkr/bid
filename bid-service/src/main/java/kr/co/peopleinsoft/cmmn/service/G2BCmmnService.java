package kr.co.peopleinsoft.cmmn.service;

import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistResultDto;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class G2BCmmnService {

	private final BidSchdulHistManageService bidSchdulHistManageService;

	public G2BCmmnService(BidSchdulHistManageService bidSchdulHistManageService) {
		this.bidSchdulHistManageService = bidSchdulHistManageService;
	}

	/**
	 * 페이지 보정
	 * <p>
	 * API 페이지 접속시 총 페이지수 및 카운트를 수집된 데이터와 비교하여 다른 경우 보정하여 수집 가능하도록 처리를 위하여..
	 * </p>
	 *
	 * @param requestDto API Request 정보가 담긴 Request 객체
	 * @param <T>        BidRequestDto 를 상속받아 구현되어진 Dto 객체
	 * @return Map 보정된 페이지 정보를 담고 있는 Map 객체
	 */
	public <T extends BidRequestDto> Map<String, Object> initPageCorrection(T requestDto) {
		Map<String, Object> map = new ConcurrentHashMap<>();
		map.put("totalCountIsNotMatch", "N");

		int startPage;
		int endPage;

		BidColctHistResultDto resultDto = bidSchdulHistManageService.selectBidColctHistResultDto(requestDto);

		endPage = requestDto.getTotalPage();

		if (resultDto == null) {
			startPage = 1;
		} else {
			// 전체 데이터가 변경된 경우 이력 정보를 변경하고 이어받기를 위한
			// 시작페이지를 변경처리하고 재계산된 전체 페이지 수만큼 수집
			if (!Objects.equals(requestDto.getTotalCount(), resultDto.getMaxTotalCnt())) {
				BidColctHistDto bidColctHistDto = BidColctHistDto.builder()
					.colctTotPage(requestDto.getTotalPage())
					.colctTotCnt(requestDto.getTotalCount())
					.colctId(requestDto.getServiceId())
					.colctBgnDt(requestDto.getInqryBgnDt())
					.colctEndDt(requestDto.getInqryEndDt())
					.build();

				bidSchdulHistManageService.updateColctPageInfo(bidColctHistDto);

				// 처음부터 재수집이 필요한 데이터가 존재하는 경우 사용
				map.put("totalCountIsNotMatch", "Y");
			}
			startPage = resultDto.getCmplColctPage() + 1;
		}

		map.put("startPage", startPage);
		map.put("endPage", endPage);

		return map;
	}
}