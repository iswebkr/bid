package kr.co.peopleinsoft.g2b.userInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.userInfo.dto.dminsttInfo.DminsttInfoResponseDto;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.userInfo.service.DminsttInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/g2b/usrInfoService")
@Tag(name = "조달청_나라장터 사용자정보 서비스 - 수요기관정보조회", description = "https://www.data.go.kr/data/15129466/openapi.do")
public class DminsttInfoController extends CmmnAbstractController {

	private static final Logger logger = LoggerFactory.getLogger(DminsttInfoController.class);

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final DminsttInfoService dminsttInfoService;

	public DminsttInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, DminsttInfoService dminsttInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.dminsttInfoService = dminsttInfoService;
	}

	@Operation(summary = "모든 사용자 정보 저장")
	@GetMapping("/saveStepDminsttInfo")
	public ResponseEntity<String> saveStepDminsttInfo() throws Exception {
		CompletableFuture<String> stepResult = CompletableFuture.supplyAsync(() -> {
			try {
				saveDminsttInfo("getDminsttInfo02", "수요기관정보조회");
			} catch (Exception e) {
				return "failure";
			}
			return "success";
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveDminsttInfo(String serviceId, String serviceDescription) throws Exception {
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
				DminsttInfoResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(DminsttInfoResponseDto.class)
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

					dminsttInfoService.batchInsertDminsttInfo(uri, pageNo, requestDto);

					// 10초
					Thread.sleep(10000);
				}

				if (startPage < endPage) {
					// 30초
					Thread.sleep(10000 * 3);
				}
			}
		}
	}
}