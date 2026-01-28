package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList.OpengResultListInfoRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList.OpengResultListInfoResponseDto;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultListInfoService;
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
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/g2b/scsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스 - 나라장터 검색조건에 의한 개찰결과 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class OpengResultListInfoController {
	private final WebClient publicWebClient;
	private final G2BCmmnService g2BCmmnService;
	private final OpengResultListInfoService opengResultListInfoService;

	public OpengResultListInfoController(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultListInfoService opengResultListInfoService) {
		this.publicWebClient = publicWebClient;
		this.g2BCmmnService = g2BCmmnService;
		this.opengResultListInfoService = opengResultListInfoService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 공사 목록 조회")
	@GetMapping("/getOpengResultListInfoCnstwkPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoCnstwkPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOpengResultListInfo("getOpengResultListInfoCnstwkPPSSrch", "나라장터 검색조건에 의한 개찰결과 공사 목록 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 용역 목록 조회")
	@GetMapping("/getOpengResultListInfoServcPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoServcPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOpengResultListInfo("getOpengResultListInfoServcPPSSrch", "나라장터 검색조건에 의한 개찰결과 용역 목록 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 외자 목록 조회")
	@GetMapping("/getOpengResultListInfoFrgcptPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoFrgcptPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOpengResultListInfo("getOpengResultListInfoFrgcptPPSSrch", "나라장터 검색조건에 의한 개찰결과 외자 목록 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 물품 목록 조회")
	@GetMapping("/getOpengResultListInfoThngPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoThngPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOpengResultListInfo("getOpengResultListInfoThngPPSSrch", "나라장터 검색조건에 의한 개찰결과 물품 목록 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveOpengResultListInfo(String serviceId, String serviceDescription) throws Exception {
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

				OpengResultListInfoRequestDto requestDto = OpengResultListInfoRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.inqryDiv(1)
					.numOfRows(100)
					.type("json")
					.build();

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
					.scheme("https")
					.host("apis.data.go.kr")
					.pathSegment("1230000/as/ScsbidInfoService", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("type", "json")
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				OpengResultListInfoResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(OpengResultListInfoResponseDto.class)
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
					opengResultListInfoService.batchInsertOpengResultListInfo(uri, pageNo, requestDto);

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