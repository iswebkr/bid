package kr.co.peopleinsoft.g2b.controller.bidPublicInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidEnum;
import kr.co.peopleinsoft.g2b.dto.bidPublicInfo.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.dto.bidPublicInfo.BidPublicInfoResponseDto;
import kr.co.peopleinsoft.g2b.service.bidPublicInfo.BidPublicInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/g2b/BidPublicInfoService")
@Tag(name = "조달청_나라장터 입찰공고정보서비스", description = "https://www.data.go.kr/data/15129394/openapi.do")
public class BidPublicInfoController {

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final BidPublicInfoService bidPublicInfoService;
	private final BidSchdulHistManageService bidSchdulHistManageService;

	public BidPublicInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService, BidSchdulHistManageService bidSchdulHistManageService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.bidPublicInfoService = bidPublicInfoService;
		this.bidSchdulHistManageService = bidSchdulHistManageService;
	}


	@Operation(summary = "모든 입찰공고 정보 수집")
	@GetMapping("/saveStepBidPublicInfo")
	public ResponseEntity<String> saveStepBidPublicInfo() throws Exception {
		CompletableFuture<String> stepResult = CompletableFuture.supplyAsync(() -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoCnstwkPPSSrch", "공사", "나라장터검색조건에 의한 입찰공고공사조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		}).thenApplyAsync(result -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoServcPPSSrch", "용역", "나라장터검색조건에 의한 입찰공고용역조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		}).thenApplyAsync(result -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoFrgcptPPSSrch", "외자", "나라장터검색조건에 의한 입찰공고외자조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		}).thenApplyAsync(result -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoThngPPSSrch", "물품", "나라장터검색조건에 의한 입찰공고물품조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription) throws Exception {
		int startYear = 2025;
		int endYear = 2026;
		int startMonth = 1;
		int endMonth = 12;

		// 현재연도의 데이터를 조회하는 경우는 현재 월까지의 자료만 수집
		if(startYear == LocalDate.now().getYear()) {
			endMonth = LocalDateTime.now().getMonthValue();
		}

		for (int targetYear = startYear; targetYear <= endYear; targetYear++) {
			for (int targetMonth = startMonth; targetMonth <= endMonth; targetMonth++) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				BidPublicInfoRequestDto bidRequestDto = BidPublicInfoRequestDto.builder()
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

				if (!bidSchdulHistManageService.colctCmplYn(bidRequestDto)) {
					UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
						.scheme("https")
						.host("apis.data.go.kr")
						.pathSegment("1230000/ad/BidPublicInfoService", bidRequestDto.getServiceId())
						.queryParam("serviceKey", bidRequestDto.getServiceKey())
						.queryParam("pageNo", 1)
						.queryParam("numOfRows", bidRequestDto.getNumOfRows())
						.queryParam("inqryDiv", bidRequestDto.getInqryDiv())
						.queryParam("type", "json")
						.queryParam("inqryBgnDt", bidRequestDto.getInqryBgnDt())
						.queryParam("inqryEndDt", bidRequestDto.getInqryEndDt());

					URI firstPageUri = uriComponentsBuilder.build().toUri();

					// 1 페이지 API 호출
					BidPublicInfoResponseDto responseDto = publicWebClient.get()
						.uri(firstPageUri)
						.retrieve()
						.bodyToMono(BidPublicInfoResponseDto.class)
						.block();

					if (responseDto == null) {
						throw new Exception("API 호출 실패");
					}

					int totalCount = responseDto.getResponse().getBody().getTotalCount();
					int totalPage = (int) Math.ceil((double) totalCount / 100);

					bidRequestDto.setTotalCount(totalCount);
					bidRequestDto.setTotalPage(totalPage);

					Map<String, Object> pageMap = g2BCmmnService.initPageCorrection(bidRequestDto);

					startPage = (Integer) pageMap.get("startPage");
					endPage = (Integer) pageMap.get("endPage");

					for (int pageNo = startPage; pageNo <= endPage; pageNo++) {
						URI uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();
						bidPublicInfoService.batchInsertPublicInfo(uri, pageNo, bidRequestDto);

						// 30초
						Thread.sleep(10000 * 3);
					}

					if (startPage < endPage) {
						// 30초
						Thread.sleep(10000 * 3);
					}
				}
			}
		}
	}
}