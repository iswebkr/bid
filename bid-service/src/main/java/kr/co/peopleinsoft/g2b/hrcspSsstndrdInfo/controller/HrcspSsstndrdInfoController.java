package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoRequestDto;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoResponseDto;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service.HrcspSsstndrdInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/g2b/hrcspSsstndrdInfoService")
@Tag(name = "나라장터 사전규격정보서비스", description = "https://www.data.go.kr/data/15129437/openapi.do")
public class HrcspSsstndrdInfoController extends CmmnAbstractController {

	private static final Logger logger = LoggerFactory.getLogger(HrcspSsstndrdInfoController.class);

	private final WebClient publicWebClient;
	private final AsyncTaskExecutor asyncTaskExecutor;
	private final G2BCmmnService g2BCmmnService;
	private final HrcspSsstndrdInfoService hrcspSsstndrdInfoService;

	public HrcspSsstndrdInfoController(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, HrcspSsstndrdInfoService hrcspSsstndrdInfoService) {
		this.publicWebClient = publicWebClient;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.g2BCmmnService = g2BCmmnService;
		this.hrcspSsstndrdInfoService = hrcspSsstndrdInfoService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 공사 목록 조회")
	@GetMapping("/getPublicPrcureThngInfoCnstwkPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoCnstwkPPSSrch() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoCnstwkPPSSrch", "나라장터 검색조건에 의한 사전규격 공사 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 용역 목록 조회")
	@GetMapping("/getPublicPrcureThngInfoServcPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoServcPPSSrch() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoServcPPSSrch", "나라장터 검색조건에 의한 사전규격 용역 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 외자 목록 조회")
	@GetMapping("/getPublicPrcureThngInfoFrgcptPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoFrgcptPPSSrch() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoFrgcptPPSSrch", "나라장터 검색조건에 의한 사전규격 외자 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 물품 목록 조회")
	@GetMapping("/getPublicPrcureThngInfoThngPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoThngPPSSrch() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoThngPPSSrch", "나라장터 검색조건에 의한 사전규격 물품 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 나라장터 검색조건에 의한 사전규격 목록 조회")
	@GetMapping("/colctThisYearPublicPrcureThngInfo")
	public ResponseEntity<String> colctThisYearPublicPrcureThngInfo() {
		List<Runnable> colcList = List.of(
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoCnstwkPPSSrch", "나라장터 검색조건에 의한 사전규격 공사 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoServcPPSSrch", "나라장터 검색조건에 의한 사전규격 용역 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoFrgcptPPSSrch", "나라장터 검색조건에 의한 사전규격 외자 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoThngPPSSrch", "나라장터 검색조건에 의한 사전규격 물품 목록 조회")
		);
		return asyncParallelProcess(colcList, asyncTaskExecutor);
	}

	private void saveThisYearPublicPrcureThngInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		savePublicPrcureThngInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void savePublicPrcureThngInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		savePublicPrcureThngInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void savePublicPrcureThngInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				HrcspSsstndrdInfoRequestDto requestDto = HrcspSsstndrdInfoRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.inqryDiv(1)
					.type("json")
					.build();

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
					.scheme("https")
					.host("apis.data.go.kr")
					.pathSegment("1230000/ao/HrcspSsstndrdInfoService", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("type", "json")
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				HrcspSsstndrdInfoResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(HrcspSsstndrdInfoResponseDto.class)
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

					hrcspSsstndrdInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, requestDto);

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