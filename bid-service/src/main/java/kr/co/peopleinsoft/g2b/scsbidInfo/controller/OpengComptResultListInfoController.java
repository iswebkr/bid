package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengComptList.OpengComptResultListInfoRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengComptList.OpengComptResultListInfoResponseDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengComptResultListInfoService;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/g2b/scsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스 - 개찰결과 개찰완료 목록 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class OpengComptResultListInfoController extends CmmnAbstractController {

	private final WebClient publicWebClient;
	private final AsyncTaskExecutor asyncTaskExecutor;
	private final G2BCmmnService g2BCmmnService;
	private final OpengComptResultListInfoService opengComptResultListInfoService;

	public OpengComptResultListInfoController(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, OpengComptResultListInfoService opengComptResultListInfoService) {
		this.publicWebClient = publicWebClient;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.g2BCmmnService = g2BCmmnService;
		this.opengComptResultListInfoService = opengComptResultListInfoService;
	}

	@Operation(summary = "입찰공고에 해당하는 모든 개찰결과 개찰완료 목록 정보 수집")
	@GetMapping("/saveStepOpengResultListInfoOpengCompt")
	public ResponseEntity<String> saveStepOpengResultListInfoOpengCompt() {
		return asyncProcess(() -> saveOpengResultListInfoOpengCompt("getOpengResultListInfoOpengCompt", "개찰결과 개찰완료 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 입찰공고에 해당하는 모든 개찰결과 개찰완료 목록 정보 수집")
	@GetMapping("/colctThisYearOpengResultListInfoOpengCompt")
	public ResponseEntity<String> colctThisYearOpengResultListInfoOpengCompt() {
		return asyncProcess(() -> saveThisYearOpengResultListInfoOpengCompt("getOpengResultListInfoOpengCompt", "개찰결과 개찰완료 목록 조회"), asyncTaskExecutor);
	}

	private void saveThisYearOpengResultListInfoOpengCompt(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveOpengResultListInfoOpengCompt(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveOpengResultListInfoOpengCompt(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveOpengResultListInfoOpengCompt(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveOpengResultListInfoOpengCompt(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
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
					throw new RuntimeException("API 호출 실패");
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
					try {
						Thread.sleep(1000 * 30);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				if (startPage < endPage) {
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
}