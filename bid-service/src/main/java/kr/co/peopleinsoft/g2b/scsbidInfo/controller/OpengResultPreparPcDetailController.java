package kr.co.peopleinsoft.g2b.scsbidInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultPreparPcDetail.OpengResultPreparPcDetailRequestDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultPreparPcDetail.OpengResultPreparPcDetailResponseDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultPreparPcDetailService;
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
@Tag(name = "나라장터 낙찰정보서비스 - 개찰결과 예비가격상세 목록 정보 수집", description = "https://www.data.go.kr/data/15129397/openapi.do")
public class OpengResultPreparPcDetailController extends G2BAbstractBidController {

	private final OpengResultPreparPcDetailService opengResultPreparPcDetailService;

	public OpengResultPreparPcDetailController(OpengResultPreparPcDetailService opengResultPreparPcDetailService) {
		this.opengResultPreparPcDetailService = opengResultPreparPcDetailService;
	}

	@Operation(summary = "개찰결과 공사 예비가격상세 목록 조회")
	@GetMapping("/getOpengResultListInfoCnstwkPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoCnstwkPreparPcDetail() {
		return asyncProcess(() -> saveOpengResultPreparPcDetailInfo("getOpengResultListInfoCnstwkPreparPcDetail", "개찰결과 공사 예비가격상세 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "개찰결과 용역 예비가격상세 목록 조회")
	@GetMapping("/getOpengResultListInfoServcPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoServcPreparPcDetail() {
		return asyncProcess(() -> saveOpengResultPreparPcDetailInfo("getOpengResultListInfoServcPreparPcDetail", "개찰결과 용역 예비가격상세 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "개찰결과 외자 예비가격상세 목록 조회")
	@GetMapping("/getOpengResultListInfoFrgcptPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoFrgcptPreparPcDetail() {
		return asyncProcess(() -> saveOpengResultPreparPcDetailInfo("getOpengResultListInfoFrgcptPreparPcDetail", "개찰결과 외자 예비가격상세 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "개찰결과 물품 예비가격상세 목록 조회")
	@GetMapping("/getOpengResultListInfoThngPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoThngPreparPcDetail() {
		return asyncProcess(() -> saveOpengResultPreparPcDetailInfo("getOpengResultListInfoThngPreparPcDetail", "개찰결과 물품 예비가격상세 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 개찰결과 물품 예비가격상세 목록 조회")
	@GetMapping("/colctThisYearOpengResultPreparPcDetailInfo")
	public ResponseEntity<String> colctThisYearOpengResultPreparPcDetailInfo() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearOpengResultPreparPcDetailInfo("getOpengResultListInfoCnstwkPreparPcDetail", "개찰결과 공사 예비가격상세 목록 조회"),
			() -> saveThisYearOpengResultPreparPcDetailInfo("getOpengResultListInfoServcPreparPcDetail", "개찰결과 용역 예비가격상세 목록 조회"),
			() -> saveThisYearOpengResultPreparPcDetailInfo("getOpengResultListInfoFrgcptPreparPcDetail", "개찰결과 외자 예비가격상세 목록 조회"),
			() -> saveThisYearOpengResultPreparPcDetailInfo("getOpengResultListInfoThngPreparPcDetail", "개찰결과 물품 예비가격상세 목록 조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearOpengResultPreparPcDetailInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveOpengResultPreparPcDetailInfo(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveOpengResultPreparPcDetailInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveOpengResultPreparPcDetailInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveOpengResultPreparPcDetailInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int totalPage;

				OpengResultPreparPcDetailRequestDto requestDto = OpengResultPreparPcDetailRequestDto.builder()
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

				OpengResultPreparPcDetailResponseDto responseDto = getResponse(OpengResultPreparPcDetailResponseDto.class, uri);

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
						opengResultPreparPcDetailService.batchInsertOpengResultPreparPcDetailInfo(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(OpengResultPreparPcDetailResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						opengResultPreparPcDetailService.batchInsertOpengResultPreparPcDetailInfo(uri, pageNo, responseDto.getItems(), requestDto);
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

	private UriComponentsBuilder getUriComponentsBuilder(OpengResultPreparPcDetailRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("apis.data.go.kr")
			.pathSegment("1230000/as/ScsbidInfoService", requestDto.getServiceId())
			.queryParam("serviceKey", requestDto.getServiceKey())
			.queryParam("pageNo", 1)
			.queryParam("inqryDiv", requestDto.getInqryDiv())
			.queryParam("numOfRows", requestDto.getNumOfRows())
			.queryParam("type", requestDto.getType())
			.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
			.queryParam("inqryEndDt", requestDto.getInqryEndDt());
	}
}