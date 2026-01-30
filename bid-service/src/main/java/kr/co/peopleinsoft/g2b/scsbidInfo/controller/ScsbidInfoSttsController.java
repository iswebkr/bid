package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusResponseDto;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
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
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/g2b/scsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스 - 나라장터 검색조건에 의한 낙찰된 목록 현황 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class ScsbidInfoSttsController {
	private final WebClient publicWebClient;
	private final G2BCmmnService g2BCmmnService;
	private final ScsbidInfoSttsService ScsbidInfoSttsService;

	public ScsbidInfoSttsController(WebClient publicWebClient, G2BCmmnService g2BCmmnService, ScsbidInfoSttsService scsbidInfoSttsService) {
		this.publicWebClient = publicWebClient;
		this.g2BCmmnService = g2BCmmnService;
		ScsbidInfoSttsService = scsbidInfoSttsService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회")
	@GetMapping("/getScsbidListSttusCnstwkPPSSrch")
	public ResponseEntity<String> getScsbidListSttusCnstwkPPSSrch() {
		CompletableFuture.runAsync(() -> {
			try {
				saveScsbidInfoStts("getScsbidListSttusCnstwkPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회")
	@GetMapping("/getScsbidListSttusServcPPSSrch")
	public ResponseEntity<String> getScsbidListSttusServcPPSSrch() {
		CompletableFuture.runAsync(() -> {
			try {
				saveScsbidInfoStts("getScsbidListSttusServcPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회")
	@GetMapping("/getScsbidListSttusFrgcptPPSSrch")
	public ResponseEntity<String> getScsbidListSttusFrgcptPPSSrch() {
		CompletableFuture.runAsync(() -> {
			try {
				saveScsbidInfoStts("getScsbidListSttusFrgcptPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회")
	@GetMapping("/getScsbidListSttusThngPPSSrch")
	public ResponseEntity<String> getScsbidListSttusThngPPSSrch() {
		CompletableFuture.runAsync(() -> {
			try {
				saveScsbidInfoStts("getScsbidListSttusThngPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "이번년도 나라장터 검색조건에 의한 낙찰된 목록 현황 조회")
	@GetMapping("/colctThisYearScsbidInfoStts")
	public ResponseEntity<String> colctThisYearScsbidInfoStts() {
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearScsbidInfoStts("getScsbidListSttusCnstwkPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회");
			} catch (Exception ignore) {
			}
		});
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearScsbidInfoStts("getScsbidListSttusServcPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회");
			} catch (Exception ignore) {
			}
		});
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearScsbidInfoStts("getScsbidListSttusFrgcptPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회");
			} catch (Exception ignore) {
			}
		});
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearScsbidInfoStts("getScsbidListSttusThngPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveThisYearScsbidInfoStts(String serviceId, String serviceDescription) throws Exception {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveScsbidInfoStts(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveScsbidInfoStts(String serviceId, String serviceDescription) throws Exception {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveScsbidInfoStts(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveScsbidInfoStts(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) throws Exception {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				ScsbidListSttusRequestDto requestDto = ScsbidListSttusRequestDto.builder()
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
					.queryParam("type", "json")
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				ScsbidListSttusResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(ScsbidListSttusResponseDto.class)
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
					ScsbidInfoSttsService.batchInsertScsbidListSttus(uri, pageNo, requestDto);

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