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

		int startPage = 1;
		int endPage;

		BidColctHistResultDto resultDto = bidSchdulHistManageService.selectBidColctHistResultDto(requestDto);

		endPage = requestDto.getTotalPage();

		// 조회된 이력이 없는 경우 첫페이지부터 데이터 수집
		if (resultDto == null) {
			map.put("startPage", startPage);
			map.put("endPage", endPage);
			return map;
		}

		// 전체카운트가 변경된 경우(새로운 데이터가 등록된 경우) 변경된 정보를 이력정보에 저장
		// 이력정보를 기반으로 새로운 수집 대상 페이지의 시작페이지 번호를 지정
		if (!Objects.equals(requestDto.getTotalCount(), resultDto.getMaxTotalCnt())) {
			BidColctHistDto bidColctHistDto = BidColctHistDto.builder()
				.colctTotPage(requestDto.getTotalPage())
				.colctTotCnt(requestDto.getTotalCount())
				.colctId(requestDto.getServiceId())
				.colctBgnDt(requestDto.getInqryBgnDt())
				.colctEndDt(requestDto.getInqryEndDt())
				.build();

			// 변경된 정보 업데이트
			bidSchdulHistManageService.updateColctPageInfo(bidColctHistDto);

			// 변경된 정보 재조회
			resultDto = bidSchdulHistManageService.selectBidColctHistResultDto(requestDto);
		}

		if(Objects.equals(resultDto.getMaxTotPage(), resultDto.getCmplColctPage()) && resultDto.getDiffCount() > 0) {
			// 아직 수집해야 할 데이터가 남아있음 여유롭게 3페이지 전 부터 재수집
			// 배치가 등록한 데이터가 제대로 카운트 된다면 수집 카운트는 맞아야겠지..
			startPage = resultDto.getCmplColctPage() - 3;
		} else {
			// 그렇지 않은 경우 마지막 수집 페이지에서 1 페이지 다음 것 부터 수집 시작
			// 없음 수집 안하고 있으면 하겠지..
			startPage = resultDto.getCmplColctPage() + 1;
		}

		// 시작페이지는 무조건 1 부터 시작
		startPage = startPage <= 0 ? 1 : startPage;
		endPage = resultDto.getMaxTotPage();

		map.put("startPage", startPage);
		map.put("endPage", endPage);

		return map;
	}
}