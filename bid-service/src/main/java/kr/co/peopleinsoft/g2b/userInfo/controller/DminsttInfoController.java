package kr.co.peopleinsoft.g2b.userInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.userInfo.dto.dminsttInfo.DminsttInfoResponseDto;
import kr.co.peopleinsoft.g2b.userInfo.service.DminsttInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/g2b/usrInfoService")
@Tag(name = "조달청_나라장터 사용자정보 서비스 - 수요기관정보조회", description = "https://www.data.go.kr/data/15129466/openapi.do")
public class DminsttInfoController extends G2BAbstractBidController {

	private final DminsttInfoService dminsttInfoService;

	public DminsttInfoController(DminsttInfoService dminsttInfoService) {
		this.dminsttInfoService = dminsttInfoService;
	}

	@Operation(summary = "모든 사용자 정보 저장")
	@GetMapping("/saveStepDminsttInfo")
	public ResponseEntity<String> saveStepDminsttInfo() {
		return asyncProcess(() -> saveDminsttInfo("getDminsttInfo02", "수요기관정보조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 모든 사용자 정보 저장")
	@GetMapping("/colctThisYearDminsttInfo")
	public ResponseEntity<String> colctThisYearDminsttInfo() {
		return asyncProcess(() -> saveThisYearDminsttInfo("getDminsttInfo02", "수요기관정보조회"), asyncTaskExecutor);
	}

	private void saveThisYearDminsttInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveDminsttInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveDminsttInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveDminsttInfo(serviceId, serviceDescription, 2000, lastYear, 1, 12);
	}

	private void saveDminsttInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
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

					dminsttInfoService.batchInsertDminsttInfo(uri, pageNo, requestDto);

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