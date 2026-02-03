package kr.co.peopleinsoft.g2b.cntrctInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoReponseDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoRequestDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.service.CntrctInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/g2b/cntrctInfoService")
@Tag(name = "나라장터 계약정보서비스", description = "https://www.data.go.kr/data/15129427/openapi.do")
public class CntrctInfoController extends G2BAbstractBidController {

	private final CntrctInfoService cntrctInfoService;

	public CntrctInfoController(CntrctInfoService cntrctInfoService) {
		this.cntrctInfoService = cntrctInfoService;
	}

	@Operation(summary = "계약현황에 대한 공사조회")
	@GetMapping("/getCntrctInfoListCnstwkPPSSrch")
	public ResponseEntity<String> getCntrctInfoListCnstwk() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListCnstwk", "계약현황에 대한 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "계약현황에 대한 용역조회")
	@GetMapping("/getCntrctInfoListServc")
	public ResponseEntity<String> getCntrctInfoListServc() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListServc", "계약현황에 대한 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "계약현황에 대한 외자조회")
	@GetMapping("/getCntrctInfoListFrgcpt")
	public ResponseEntity<String> getCntrctInfoListFrgcpt() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListFrgcpt", "계약현황에 대한 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "계약현황에 대한 물품조회")
	@GetMapping("/getCntrctInfoListThng")
	public ResponseEntity<String> getCntrctInfoListThng() {
		return asyncProcess(() -> saveCntrctInfo("getCntrctInfoListThng", "계약현황에 대한 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 수집")
	@GetMapping("/colctThisYearCntrctInfo")
	public ResponseEntity<String> colctThisYearCntrctInfo() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearCntrctInfo("getCntrctInfoListCnstwk", "계약현황에 대한 공사조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListServc", "계약현황에 대한 용역조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListFrgcpt", "계약현황에 대한 외자조회"),
			() -> saveThisYearCntrctInfo("getCntrctInfoListThng", "계약현황에 대한 물품조회")
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
				int totalPage;

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

				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				CntrctInfoReponseDto responseDto = getResponse(CntrctInfoReponseDto.class, uri);

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
						cntrctInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(CntrctInfoReponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						cntrctInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, responseDto.getItems(), requestDto);
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

	private UriComponentsBuilder getUriComponentsBuilder(CntrctInfoRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
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
	}
}