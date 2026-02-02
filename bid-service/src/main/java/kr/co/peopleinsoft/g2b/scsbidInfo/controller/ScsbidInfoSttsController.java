package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusResponseDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/g2b/scsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스 - 나라장터 검색조건에 의한 낙찰된 목록 현황 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class ScsbidInfoSttsController extends G2BAbstractBidController {

	private final ScsbidInfoSttsService ScsbidInfoSttsService;

	public ScsbidInfoSttsController(ScsbidInfoSttsService scsbidInfoSttsService) {
		ScsbidInfoSttsService = scsbidInfoSttsService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회")
	@GetMapping("/getScsbidListSttusCnstwkPPSSrch")
	public ResponseEntity<String> getScsbidListSttusCnstwkPPSSrch() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusCnstwkPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회")
	@GetMapping("/getScsbidListSttusServcPPSSrch")
	public ResponseEntity<String> getScsbidListSttusServcPPSSrch() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusServcPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회")
	@GetMapping("/getScsbidListSttusFrgcptPPSSrch")
	public ResponseEntity<String> getScsbidListSttusFrgcptPPSSrch() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusFrgcptPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회")
	@GetMapping("/getScsbidListSttusThngPPSSrch")
	public ResponseEntity<String> getScsbidListSttusThngPPSSrch() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusThngPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 나라장터 검색조건에 의한 낙찰된 목록 현황 조회")
	@GetMapping("/colctThisYearScsbidInfoStts")
	public ResponseEntity<String> colctThisYearScsbidInfoStts() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusCnstwkPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusServcPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusFrgcptPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusThngPPSSrch", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearScsbidInfoStts(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveScsbidInfoStts(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveScsbidInfoStts(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveScsbidInfoStts(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveScsbidInfoStts(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
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
					ScsbidInfoSttsService.batchInsertScsbidListSttus(uri, pageNo, requestDto);

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