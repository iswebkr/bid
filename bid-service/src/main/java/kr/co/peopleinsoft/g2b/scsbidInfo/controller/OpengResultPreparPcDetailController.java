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
import java.util.Map;

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
				int endPage;

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

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
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

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				OpengResultPreparPcDetailResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(OpengResultPreparPcDetailResponseDto.class)
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
					opengResultPreparPcDetailService.batchInsertOpengResultPreparPcDetailInfo(uri, pageNo, requestDto);

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