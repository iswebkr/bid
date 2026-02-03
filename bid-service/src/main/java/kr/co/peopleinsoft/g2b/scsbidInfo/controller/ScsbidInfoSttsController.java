package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusResponseDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
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
@RequestMapping("/g2b/scsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스 - 낙찰목록 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class ScsbidInfoSttsController extends G2BAbstractBidController {

	private final ScsbidInfoSttsService scsbidInfoSttsService;

	public ScsbidInfoSttsController(ScsbidInfoSttsService scsbidInfoSttsService) {
		this.scsbidInfoSttsService = scsbidInfoSttsService;
	}


	@Operation(summary = "낙찰된 목록 현황 공사조회")
	@GetMapping("/getScsbidListSttusCnstwk")
	public ResponseEntity<String> getScsbidListSttusCnstwk() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusCnstwk", "낙찰된 목록 현황 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "낙찰된 목록 현황 용역조회")
	@GetMapping("/getScsbidListSttusServc")
	public ResponseEntity<String> getScsbidListSttusServc() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusServc", "낙찰된 목록 현황 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "낙찰된 목록 현황 외자조회")
	@GetMapping("/getScsbidListSttusFrgcpt")
	public ResponseEntity<String> getScsbidListSttusFrgcpt() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusFrgcpt", "낙찰된 목록 현황 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "낙찰된 목록 현황 물품조회")
	@GetMapping("/getScsbidListSttusThng")
	public ResponseEntity<String> getScsbidListSttusThng() {
		return asyncProcess(() -> saveScsbidInfoStts("getScsbidListSttusThng", "낙찰된 목록 현황 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "최신 낙찰목록 수집")
	@GetMapping("/colctThisYearScsbidInfoStts")
	public ResponseEntity<String> colctThisYearScsbidInfoStts() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusCnstwk", "낙찰된 목록 현황 공사조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusServc", "낙찰된 목록 현황 용역조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusFrgcpt", "낙찰된 목록 현황 외자조회"),
			() -> saveThisYearScsbidInfoStts("getScsbidListSttusThng", "낙찰된 목록 현황 물품조회")
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
				int totalPage;

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

				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				ScsbidListSttusResponseDto responseDto = getResponse(ScsbidListSttusResponseDto.class, uri);

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
						scsbidInfoSttsService.batchInsertScsbidListSttus(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(ScsbidListSttusResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						scsbidInfoSttsService.batchInsertScsbidListSttus(uri, pageNo, responseDto.getItems(), requestDto);
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

	private UriComponentsBuilder getUriComponentsBuilder(ScsbidListSttusRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
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
	}
}