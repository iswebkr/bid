package kr.co.peopleinsoft.g2b.orderPlanSttus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusRequestDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusResponseDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.springframework.core.task.AsyncTaskExecutor;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/g2b/orderPlanSttusService")
@Tag(name = "나라장터 발주계획현황서비스", description = "https://www.data.go.kr/data/15129462/openapi.do")
public class OrderPlanSttusController extends CmmnAbstractController {

	private final WebClient publicWebClient;
	private final AsyncTaskExecutor asyncTaskExecutor;
	private final G2BCmmnService g2BCmmnService;
	private final OrderPlanSttusService orderPlanSttusService;

	public OrderPlanSttusController(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, OrderPlanSttusService orderPlanSttusService) {
		this.publicWebClient = publicWebClient;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.g2BCmmnService = g2BCmmnService;
		this.orderPlanSttusService = orderPlanSttusService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회")
	@GetMapping("/getOrderPlanSttusListCnstwkPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListCnstwkPPSSrch() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListCnstwkPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회")
	@GetMapping("/getOrderPlanSttusListServcPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListServcPPSSrch() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListServcPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회")
	@GetMapping("/getOrderPlanSttusListFrgcptPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListFrgcptPPSSrch() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListFrgcptPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회")
	@GetMapping("/getOrderPlanSttusListThngPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListThngPPSSrch() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListThngPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "최신 발주계획현황 데이터 수집")
	@GetMapping("/colctThisYearOrderPlanSttus")
	public ResponseEntity<String> colctThisYearOrderPlanSttus() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListCnstwkPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListServcPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListFrgcptPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListThngPPSSrch", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearOrderPlanSttus(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveOrderPlanSttus(serviceId, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveOrderPlanSttus(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveOrderPlanSttus(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveOrderPlanSttus(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
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
					orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, requestDto);

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