package kr.co.peopleinsoft.g2b.controller.useInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.g2b.dto.bidPublicInfo.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.dto.cmmn.BidEnum;
import kr.co.peopleinsoft.g2b.dto.userInfo.dminsttInfo.DminsttInfoResponseDto;
import kr.co.peopleinsoft.g2b.service.usrInfo.DminsttInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/g2b/usrInfoService")
@Tag(name = "조달청_나라장터 사용자정보 서비스 - 수요기관정보조회", description = "https://www.data.go.kr/data/15129466/openapi.do")
public class DminsttInfoController extends CmmnAbstractController {

	private static final Logger logger = LoggerFactory.getLogger(DminsttInfoController.class);

	private final G2BCmmnService g2BCmmnService;
	private final WebClient publicWebClient;
	private final DminsttInfoService dminsttInfoService;
	private final BidSchdulHistManageService g2BSchdulHistManageService;

	AtomicInteger atomicRowCnt = new AtomicInteger(0);

	public DminsttInfoController(G2BCmmnService g2BCmmnService, WebClient publicWebClient, DminsttInfoService dminsttInfoService, BidSchdulHistManageService g2BSchdulHistManageService) {
		this.g2BCmmnService = g2BCmmnService;
		this.publicWebClient = publicWebClient;
		this.dminsttInfoService = dminsttInfoService;
		this.g2BSchdulHistManageService = g2BSchdulHistManageService;
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

		int startYear = 2000;
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

				BidPublicInfoRequestDto bidRequestDto = BidPublicInfoRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.inqryDiv(1)
					.type("json")
					.build();

				// serviceId 에 해당하는 해당 기간(inqryBgnDt ~ inqryEndDt) 에 수집완료된 데이터는 수집 대상에서 제외
				if (!g2BSchdulHistManageService.colctCmplYn(bidRequestDto)) {
					UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
						.scheme("https")
						.host("apis.data.go.kr")
						.pathSegment("1230000/ao/UsrInfoService02", bidRequestDto.getServiceId())
						.queryParam("serviceKey", bidRequestDto.getServiceKey())
						.queryParam("pageNo", 1)
						.queryParam("numOfRows", bidRequestDto.getNumOfRows())
						.queryParam("inqryDiv", bidRequestDto.getInqryDiv())
						.queryParam("type", "json")
						.queryParam("inqryBgnDt", bidRequestDto.getInqryBgnDt())
						.queryParam("inqryEndDt", bidRequestDto.getInqryEndDt());

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

					bidRequestDto.setTotalCount(totalCount);
					bidRequestDto.setTotalPage(totalPage);

					Map<String, Object> pageMap = g2BCmmnService.initPageCorrection(bidRequestDto);

					startPage = (Integer) pageMap.get("startPage");
					endPage = (Integer) pageMap.get("endPage");

					for (int pageNo = startPage; pageNo <= endPage; pageNo++) {
						URI uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						dminsttInfoService.batchInsertDminsttInfo(uri, pageNo, bidRequestDto);

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
}