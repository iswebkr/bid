package kr.co.peopleinsoft.g2b.orderPlanSttus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusRequestDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusResponseDto;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
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
@RequestMapping("/g2b/orderPlanSttusService")
@Tag(name = "나라장터 발주계획현황서비스", description = "https://www.data.go.kr/data/15129462/openapi.do")
public class OrderPlanSttusController {

	private final WebClient publicWebClient;
	private final G2BCmmnService g2BCmmnService;
	private final OrderPlanSttusService orderPlanSttusService;

	public OrderPlanSttusController(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OrderPlanSttusService orderPlanSttusService) {
		this.publicWebClient = publicWebClient;
		this.g2BCmmnService = g2BCmmnService;
		this.orderPlanSttusService = orderPlanSttusService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회")
	@GetMapping("/getOrderPlanSttusListCnstwkPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListCnstwkPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOrderPlanSttus("getOrderPlanSttusListCnstwkPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회")
	@GetMapping("/getOrderPlanSttusListServcPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListServcPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOrderPlanSttus("getOrderPlanSttusListServcPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회")
	@GetMapping("/getOrderPlanSttusListFrgcptPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListFrgcptPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOrderPlanSttus("getOrderPlanSttusListFrgcptPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회")
	@GetMapping("/getOrderPlanSttusListThngPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListThngPPSSrch() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveOrderPlanSttus("getOrderPlanSttusListThngPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "최신 발주계획현황 데이터 수집")
	@GetMapping("/colctThisYearData")
	public ResponseEntity<String> colctThisYearData() throws Exception {
		CompletableFuture.runAsync(() -> {
			try {
				saveThisYearOrderPlanSttus("getOrderPlanSttusListCnstwkPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회");
				saveThisYearOrderPlanSttus("getOrderPlanSttusListServcPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회");
				saveThisYearOrderPlanSttus("getOrderPlanSttusListFrgcptPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회");
				saveThisYearOrderPlanSttus("getOrderPlanSttusListThngPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회");
			} catch (Exception ignore) {
			}
		});
		return ResponseEntity.ok().body("success");
	}

	private void saveThisYearOrderPlanSttus(String serviceId, String serviceDescription) throws Exception {
		saveOrderPlanSttus(serviceId, serviceDescription, 2026, 2026, 12, 1);
	}

	private void saveOrderPlanSttus(String serviceId, String serviceDescription) throws Exception {
		saveOrderPlanSttus(serviceId, serviceDescription, 2026, 2020, 12, 1);
	}

	private void saveOrderPlanSttus(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) throws Exception {
		/*int startYear = 2026;
		int endYear = 2020;
		int startMonth = 12;
		int endMonth = 1;*/

		for (int targetYear = startYear; targetYear >= endYear; targetYear--) {

			if (LocalDate.now().getYear() == targetYear) {
				startMonth = LocalDate.now().getMonthValue();
			} else {
				startMonth = 12;
			}

			for (int targetMonth = startMonth; targetMonth >= endMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String orderBgnYm = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
				String orderEndYm = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMM"));
				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int endPage;

				OrderPlanSttusRequestDto requestDto = OrderPlanSttusRequestDto.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.orderBgnYm(orderBgnYm)
					.orderEndYm(orderEndYm)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.type("json")
					.build();

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
					.scheme("https")
					.host("apis.data.go.kr")
					.pathSegment("1230000/ao/OrderPlanSttusService", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("type", "json")
					.queryParam("orderBgnYm", requestDto.getOrderBgnYm())
					.queryParam("orderEndYm", requestDto.getOrderEndYm())
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				OrderPlanSttusResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(OrderPlanSttusResponseDto.class)
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
					orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, requestDto);

					// 30초
					Thread.sleep(1000 * 30);
				}

				if (startPage < endPage) {
					// 30초
					Thread.sleep(1000 * 30);
				}
			}
		}
	}
}