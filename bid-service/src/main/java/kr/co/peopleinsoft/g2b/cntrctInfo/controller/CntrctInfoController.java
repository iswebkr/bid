package kr.co.peopleinsoft.g2b.cntrctInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoReponseDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoRequestDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.service.CntrctInfoService;
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
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/g2b/cntrctInfoService")
@Tag(name = "나라장터 계약정보서비스", description = "https://www.data.go.kr/data/15129427/openapi.do")
public class CntrctInfoController extends CmmnAbstractController {

	private final G2BCmmnService g2BCmmnService;
	private final AsyncTaskExecutor asyncTaskExecutor;
	private final WebClient publicWebClient;
	private final CntrctInfoService cntrctInfoService;

	public CntrctInfoController(G2BCmmnService g2BCmmnService, AsyncTaskExecutor asyncTaskExecutor, WebClient publicWebClient, CntrctInfoService cntrctInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.publicWebClient = publicWebClient;
		this.cntrctInfoService = cntrctInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 공사조회")
	@GetMapping("/getCntrctInfoListCnstwkPPSSrch")
	public ResponseEntity<String> getCntrctInfoListCnstwkPPSSrch() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListCnstwkPPSSrch", "나라장터검색조건에 의한 계약현황 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 용역조회")
	@GetMapping("/getCntrctInfoListServcPPSSrch")
	public ResponseEntity<String> getCntrctInfoListServcPPSSrch() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListServcPPSSrch", "나라장터검색조건에 의한 계약현황 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 외자조회")
	@GetMapping("/getCntrctInfoListFrgcptPPSSrch")
	public ResponseEntity<String> getCntrctInfoListFrgcptPPSSrch() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListFrgcptPPSSrch", "나라장터검색조건에 의한 계약현황 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 물품조회")
	@GetMapping("/getCntrctInfoListThngPPSSrch")
	public ResponseEntity<String> getCntrctInfoListThngPPSSrch() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListThngPPSSrch", "나라장터검색조건에 의한 계약현황 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 수집")
	@GetMapping("/colctThisYearCntrctInfo")
	public ResponseEntity<String> colctThisYearCntrctInfo() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearCntrctInfo("getCntrctInfoListCnstwkPPSSrch", "나라장터검색조건에 의한 계약현황 공사조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListServcPPSSrch", "나라장터검색조건에 의한 계약현황 용역조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListFrgcptPPSSrch", "나라장터검색조건에 의한 계약현황 외자조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListThngPPSSrch", "나라장터검색조건에 의한 계약현황 물품조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearCntrctInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveCntrctInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveCntrctInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveCntrctInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveCntrctInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "01";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

				int startPage;
				int endPage;

				CntrctInfoRequestDto requestDto = CntrctInfoRequestDto.builder()
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
					.pathSegment("1230000/ao/CntrctInfoService", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("type", "json")
					.queryParam("inqryBgnDate", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDate", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				CntrctInfoReponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(CntrctInfoReponseDto.class)
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

					cntrctInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, requestDto);

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