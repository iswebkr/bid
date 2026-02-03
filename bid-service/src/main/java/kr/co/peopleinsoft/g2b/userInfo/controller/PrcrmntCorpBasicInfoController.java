package kr.co.peopleinsoft.g2b.userInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoRequestDto;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoResponseDto;
import kr.co.peopleinsoft.g2b.userInfo.service.PrcrmntCorpBasicInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/g2b/usrInfoService")
@Tag(name = "조달청_나라장터 사용자정보 서비스 - 조달업체 기본정보 조회", description = "https://www.data.go.kr/data/15129466/openapi.do")
public class PrcrmntCorpBasicInfoController extends G2BAbstractBidController {

	private final PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService;

	public PrcrmntCorpBasicInfoController(PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService) {
		this.prcrmntCorpBasicInfoService = prcrmntCorpBasicInfoService;
	}

	@Operation(summary = "조달업체 기본정보 / 업종정보 / 공급물품정보 수집")
	@GetMapping("/saveStepPrcrmntCorpBasicInfo")
	public ResponseEntity<String> saveStepPrcrmntCorpBasicInfo() {
		return asyncProcess(() -> savePrcrmntCorpBasicInfo("getPrcrmntCorpBasicInfo02", "조달업체 기본정보 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 조달업체 기본정보 / 업종정보 / 공급물품정보 수집")
	@GetMapping("/colctThisYearPrcrmntCorpBasicInfo")
	public ResponseEntity<String> colctThisYearPrcrmntCorpBasicInfo() {
		return asyncProcess(() -> saveThisYearPrcrmntCorpBasicInfo("getPrcrmntCorpBasicInfo02", "조달업체 기본정보 조회"), asyncTaskExecutor);
	}

	private void saveThisYearPrcrmntCorpBasicInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		savePrcrmntCorpBasicInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void savePrcrmntCorpBasicInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		savePrcrmntCorpBasicInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void savePrcrmntCorpBasicInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int totalPage;

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

				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				PrcrmntCorpBasicInfoResponseDto responseDto = getResponse(PrcrmntCorpBasicInfoResponseDto.class, uri);

				if (responseDto == null) {
					return;
				}

				// 페이지 설정 (이전에 수집된 페이지를 기반으로 startPage 재설정)
				startPage = bidSchdulHistManageService.getStartPage(requestDto);
				totalPage = responseDto.getTotalPage();

				requestDto.setTotalCount(responseDto.getTotalCount());
				requestDto.setTotalPage(responseDto.getTotalPage());

				for (int pageNo = startPage; pageNo <= totalPage; pageNo++) {
					if (pageNo == 1) {
						prcrmntCorpBasicInfoService.batchInsertPrcrmntCorpBasicInfo(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(PrcrmntCorpBasicInfoResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						prcrmntCorpBasicInfoService.batchInsertPrcrmntCorpBasicInfo(uri, pageNo, responseDto.getItems(), requestDto);
					}

					try {
						Thread.sleep(1000 * 20);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private UriComponentsBuilder getUriComponentsBuilder(PrcrmntCorpBasicInfoRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
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
	}
}