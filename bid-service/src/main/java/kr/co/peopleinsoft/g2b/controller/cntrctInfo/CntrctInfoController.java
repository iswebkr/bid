package kr.co.peopleinsoft.g2b.controller.cntrctInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidEnum;
import kr.co.peopleinsoft.g2b.dto.cntrctInfo.CntrctInfoReponseDto;
import kr.co.peopleinsoft.g2b.dto.cntrctInfo.CntrctInfoRequestDto;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.cntrctInfo.CntrctInfoService;
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
@RequestMapping("/g2b/cntrctInfoService")
@Tag(name = "나라장터 계약정보서비스", description = "https://www.data.go.kr/data/15129427/openapi.do")
public class CntrctInfoController {

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final CntrctInfoService cntrctInfoService;

	public CntrctInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, CntrctInfoService cntrctInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.cntrctInfoService = cntrctInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 공사조회")
	@GetMapping("/getCntrctInfoListCnstwkPPSSrch")
	public ResponseEntity<String> getCntrctInfoListCnstwkPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveCntrctInfo("getCntrctInfoListCnstwkPPSSrch", "나라장터검색조건에 의한 계약현황 공사조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 용역조회")
	@GetMapping("/getCntrctInfoListServcPPSSrch")
	public ResponseEntity<String> getCntrctInfoListServcPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveCntrctInfo("getCntrctInfoListServcPPSSrch", "나라장터검색조건에 의한 계약현황 용역조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 외자조회")
	@GetMapping("/getCntrctInfoListFrgcptPPSSrch")
	public ResponseEntity<String> getCntrctInfoListFrgcptPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveCntrctInfo("getCntrctInfoListFrgcptPPSSrch", "나라장터검색조건에 의한 계약현황 외자조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 물품조회")
	@GetMapping("/getCntrctInfoListThngPPSSrch")
	public ResponseEntity<String> getCntrctInfoListThngPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveCntrctInfo("getCntrctInfoListThngPPSSrch", "나라장터검색조건에 의한 계약현황 물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveCntrctInfo(String serviceId, String serviceDescription) throws Exception {
		int startYear = 2026;
		int endYear = 2020;
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
					cntrctInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, requestDto);

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