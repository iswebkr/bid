package kr.co.peopleinsoft.g2b.controller.scsbidInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidEnum;
import kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengComptList.OpengComptResultListInfoRequestDto;
import kr.co.peopleinsoft.g2b.dto.scsbidInfo.opengComptList.OpengComptResultListInfoResponseDto;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.OpengComptResultListInfoService;
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
@Tag(name = "나라장터 낙찰정보서비스 - 개찰결과 개찰완료 목록 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class OpengComptResultListInfoController {
	private final WebClient publicWebClient;
	private final G2BCmmnService g2BCmmnService;
	private final OpengComptResultListInfoService opengComptResultListInfoService;
	private final BidSchdulHistManageService schdulHistManageService;

	public OpengComptResultListInfoController(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengComptResultListInfoService opengComptResultListInfoService, BidSchdulHistManageService schdulHistManageService) {
		this.publicWebClient = publicWebClient;
		this.g2BCmmnService = g2BCmmnService;
		this.opengComptResultListInfoService = opengComptResultListInfoService;
		this.schdulHistManageService = schdulHistManageService;
	}

	@Operation(summary = "입찰공고에 해당하는 모든 개찰결과 개찰완료 목록 정보 수집")
	@GetMapping("/saveStepOpengResultListInfoOpengCompt")
	public ResponseEntity<String> saveStepOpengResultListInfoOpengCompt() throws ExecutionException, InterruptedException {
		CompletableFuture<String> stepResult = CompletableFuture.supplyAsync(() -> {
			try {
				saveOpengResultListInfoOpengCompt("getOpengResultListInfoOpengCompt", "개찰결과 개찰완료 목록 조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		});
		return ResponseEntity.ok().body(stepResult.get());
	}

	private void saveOpengResultListInfoOpengCompt(String serviceId, String serviceDescription) throws Exception {
		int startYear = 2025;
		int endYear = 2026;
		int startMonth = 1;
		int endMonth = 12; // 이번달 자료까지만

		// 현재연도의 데이터를 조회하는 경우는 현재 월까지의 자료만 수집
		if (startYear == LocalDate.now().getYear()) {
			endMonth = LocalDateTime.now().getMonthValue();
		}

		for (int targetYear = startYear; targetYear <= endYear; targetYear++) {
			for (int targetMonth = startMonth; targetMonth <= endMonth; targetMonth++) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				OpengComptResultListInfoRequestDto requestDto = OpengComptResultListInfoRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.inqryDiv(1)
					.numOfRows(100)
					.type("json")
					.build();

				if (!schdulHistManageService.colctCmplYn(requestDto)) {
					UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
						.scheme("https")
						.host("apis.data.go.kr")
						.pathSegment("1230000/as/ScsbidInfoService", requestDto.getServiceId())
						.queryParam("serviceKey", requestDto.getServiceKey())
						.queryParam("pageNo", 1)
						.queryParam("numOfRows", requestDto.getNumOfRows())
						.queryParam("bidNtceNo", requestDto.getBidNtceNo()) // 필수 (입찰공고번호)
						.queryParam("type", "json");

					URI firstPageUri = uriComponentsBuilder.build().toUri();

					// 1 페이지 API 호출
					OpengComptResultListInfoResponseDto responseDto = publicWebClient.get()
						.uri(firstPageUri)
						.retrieve()
						.bodyToMono(OpengComptResultListInfoResponseDto.class)
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
						opengComptResultListInfoService.batchInsertOpengResultListInfo(uri, pageNo, requestDto);

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