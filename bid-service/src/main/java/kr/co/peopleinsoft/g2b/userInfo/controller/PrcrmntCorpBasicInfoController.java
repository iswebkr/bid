package kr.co.peopleinsoft.g2b.userInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoRequestDto;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoResponseDto;
import kr.co.peopleinsoft.g2b.userInfo.service.PrcrmntCorpBasicInfoService;
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
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/g2b/usrInfoService")
@Tag(name = "조달청_나라장터 사용자정보 서비스 - 조달업체 기본정보 조회", description = "https://www.data.go.kr/data/15129466/openapi.do")
public class PrcrmntCorpBasicInfoController {

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService;

	public PrcrmntCorpBasicInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.prcrmntCorpBasicInfoService = prcrmntCorpBasicInfoService;
	}

	@Operation(summary = "조달업체 기본정보 / 업종정보 / 공급물품정보 수집")
	@GetMapping("/saveStepPrcrmntCorpBasicInfo")
	public ResponseEntity<String> saveStepPrcrmntCorpBasicInfo() {
		CompletableFuture.runAsync(() -> {
			try {
				savePrcrmntCorpBasicInfo("getPrcrmntCorpBasicInfo02", "조달업체 기본정보 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "이번년도 조달업체 기본정보 / 업종정보 / 공급물품정보 수집")
	@GetMapping("/colctThisYearPrcrmntCorpBasicInfo")
	public ResponseEntity<String> colctThisYearPrcrmntCorpBasicInfo() {
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearPrcrmntCorpBasicInfo("getPrcrmntCorpBasicInfo02", "조달업체 기본정보 조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveThisYearPrcrmntCorpBasicInfo(String serviceId, String serviceDescription) throws Exception {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		savePrcrmntCorpBasicInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void savePrcrmntCorpBasicInfo(String serviceId, String serviceDescription) throws Exception {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		savePrcrmntCorpBasicInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void savePrcrmntCorpBasicInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) throws Exception {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				PrcrmntCorpBasicInfoRequestDto requestDto = PrcrmntCorpBasicInfoRequestDto.builder()
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
					.pathSegment("1230000/ao/UsrInfoService02", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("type", "json")
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				PrcrmntCorpBasicInfoResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(PrcrmntCorpBasicInfoResponseDto.class)
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

					prcrmntCorpBasicInfoService.batchInsertPrcrmntCorpBasicInfo(uri, pageNo, requestDto);

					// 10초
					Thread.sleep(10000);
				}

				if (startPage < endPage) {
					// 30초
					Thread.sleep(1000 * 30);
				}
			}
		}
	}
}