package kr.co.peopleinsoft.g2b.bidPublicInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoResponseDto;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/g2b/bidPublicInfoService")
@Tag(name = "조달청_나라장터 입찰공고정보서비스", description = "https://www.data.go.kr/data/15129394/openapi.do")
public class BidPublicInfoController {

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final BidPublicInfoService bidPublicInfoService;

	public BidPublicInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.bidPublicInfoService = bidPublicInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고공사조회")
	@GetMapping("/getBidPblancListInfoCnstwkPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoCnstwkPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoCnstwkPPSSrch", "공사", "나라장터검색조건에 의한 입찰공고공사조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고외자조회")
	@GetMapping("/getBidPblancListInfoFrgcptPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoFrgcptPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoFrgcptPPSSrch", "외자", "나라장터검색조건에 의한 입찰공고외자조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고용역조회")
	@GetMapping("/getBidPblancListInfoServcPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoServcPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoServcPPSSrch", "용역", "나라장터검색조건에 의한 입찰공고용역조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고물품조회")
	@GetMapping("/getBidPblancListInfoThngPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoThngPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveBidPblancListInfo("getBidPblancListInfoThngPPSSrch", "물품", "나라장터검색조건에 의한 입찰공고물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription) throws Exception {
		int startYear = 2026;
		int endYear = 2025;
		int startMonth = 12;
		int endMonth = 1;

		for (int targetYear = startYear; targetYear >= endYear; targetYear--) {

			if (LocalDate.now().getYear() == targetYear) {
				startMonth = LocalDate.now().getMonthValue();
			} else {
				startMonth = 12;
			}

			for (int targetMonth = startMonth; targetMonth >= endMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

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

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
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

				requestDto.setTotalCount(totalCount);
				requestDto.setTotalPage(totalPage);

				Map<String, Object> pageMap = g2BCmmnService.initPageCorrection(requestDto);

				startPage = (Integer) pageMap.get("startPage");
				endPage = (Integer) pageMap.get("endPage");

				for (int pageNo = startPage; pageNo <= endPage; pageNo++) {
					URI uri = uriComponentsBuilder.cloneBuilder()
						.replaceQueryParam("pageNo", pageNo)
						.build().toUri();
					bidPublicInfoService.batchInsertPublicInfo(uri, pageNo, requestDto);

					// 30초
					Thread.sleep(1000 * 30);
				}

				if (startPage < endPage) {
					// 30초
					Thread.sleep(1000 * 30);
				}
			}
		}
	}
}