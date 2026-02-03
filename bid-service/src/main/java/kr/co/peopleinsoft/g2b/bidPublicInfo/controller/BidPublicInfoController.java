package kr.co.peopleinsoft.g2b.bidPublicInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoResponseDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/g2b/bidPublicInfoService")
@Tag(name = "조달청_나라장터 입찰공고정보서비스", description = "https://www.data.go.kr/data/15129394/openapi.do")
public class BidPublicInfoController extends G2BAbstractBidController {

	private final BidPublicInfoService bidPublicInfoService;

	public BidPublicInfoController(BidPublicInfoService bidPublicInfoService) {
		this.bidPublicInfoService = bidPublicInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고공사조회")
	@GetMapping("/getBidPblancListInfoCnstwk")
	public ResponseEntity<String> getBidPblancListInfoCnstwk() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoCnstwk", "공사", "입찰공고목록 정보에 대한 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고용역조회")
	@GetMapping("/getBidPblancListInfoServc")
	public ResponseEntity<String> getBidPblancListInfoServc() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoServc", "용역", "입찰공고목록 정보에 대한 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고외자조회")
	@GetMapping("/getBidPblancListInfoFrgcpt")
	public ResponseEntity<String> getBidPblancListInfoFrgcpt() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoFrgcpt", "외자", "입찰공고목록 정보에 대한 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고물품조회")
	@GetMapping("/getBidPblancListInfoThng")
	public ResponseEntity<String> getBidPblancListInfoThng() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoThng", "물품", "입찰공고목록 정보에 대한 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "입찰공고 이번 년도 데이터만 조회")
	@GetMapping("/colctThisYearBidPblancListInfo")
	public ResponseEntity<String> colctThisYearBidPblancListInfo() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoCnstwk", "공사", "입찰공고목록 정보에 대한 공사조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoServc", "용역", "입찰공고목록 정보에 대한 용역조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoFrgcpt", "외자", "입찰공고목록 정보에 대한 외자조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoThng", "물품", "입찰공고목록 정보에 대한 물품조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearBidPblancListInfo(String serviceId, String bidType, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveBidPblancListInfo(serviceId, bidType, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveBidPblancListInfo(serviceId, bidType, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int totalPage;

				BidPublicInfoRequestDto requestDto = BidPublicInfoRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.bidType(bidType)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.inqryDiv(1)
					.type("json")
					.build();

				// URI 를 빌드하고
				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				BidPublicInfoResponseDto responseDto = getResponse(BidPublicInfoResponseDto.class, uri);

				if (responseDto == null) {
					return;
				}

				// 페이지 설정 (이전에 수집된 페이지를 기반으로 startPage 재설정)
				startPage = bidSchdulHistManageService.getStartPage(requestDto);
				totalPage = responseDto.getTotalPage();

				requestDto.setTotalCount(responseDto.getTotalCount());
				requestDto.setTotalPage(responseDto.getTotalPage());

				for (int pageNo = startPage; pageNo <= totalPage; pageNo++) {
					if (pageNo == 1) {
						bidPublicInfoService.batchInsertPublicInfo(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(BidPublicInfoResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						// 수집 데이터 정보 저장
						bidPublicInfoService.batchInsertPublicInfo(uri, pageNo, responseDto.getItems(), requestDto);
					}

					// 30초
					try {
						Thread.sleep(1000 * 30);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private UriComponentsBuilder getUriComponentsBuilder(BidPublicInfoRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("apis.data.go.kr")
			.pathSegment("1230000/ad/BidPublicInfoService", requestDto.getServiceId())
			.queryParam("serviceKey", requestDto.getServiceKey())
			.queryParam("pageNo", 1)
			.queryParam("numOfRows", requestDto.getNumOfRows())
			.queryParam("inqryDiv", requestDto.getInqryDiv())
			.queryParam("type", "json")
			.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
			.queryParam("inqryEndDt", requestDto.getInqryEndDt());
	}
}