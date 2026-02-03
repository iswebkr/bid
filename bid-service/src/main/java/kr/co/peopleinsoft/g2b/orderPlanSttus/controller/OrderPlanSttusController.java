package kr.co.peopleinsoft.g2b.orderPlanSttus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusRequestDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusResponseDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
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
@RequestMapping("/g2b/orderPlanSttusService")
@Tag(name = "나라장터 발주계획현황서비스", description = "https://www.data.go.kr/data/15129462/openapi.do")
public class OrderPlanSttusController extends G2BAbstractBidController {

	private final OrderPlanSttusService orderPlanSttusService;

	public OrderPlanSttusController(OrderPlanSttusService orderPlanSttusService) {
		this.orderPlanSttusService = orderPlanSttusService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회")
	@GetMapping("/getOrderPlanSttusListCnstwk")
	public ResponseEntity<String> getOrderPlanSttusListCnstwk() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListCnstwk", "발주계획현황에 대한 공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회")
	@GetMapping("/getOrderPlanSttusListServc")
	public ResponseEntity<String> getOrderPlanSttusListServc() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListServc", "발주계획현황에 대한 용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회")
	@GetMapping("/getOrderPlanSttusListFrgcpt")
	public ResponseEntity<String> getOrderPlanSttusListFrgcpt() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListFrgcpt", "발주계획현황에 대한 외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회")
	@GetMapping("/getOrderPlanSttusListThng")
	public ResponseEntity<String> getOrderPlanSttusListThng() {
		return asyncProcess(() -> saveOrderPlanSttus("getOrderPlanSttusListThng", "발주계획현황에 대한 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "최신 발주계획현황 데이터 수집")
	@GetMapping("/colctThisYearOrderPlanSttus")
	public ResponseEntity<String> colctThisYearOrderPlanSttus() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListCnstwk", "발주계획현황에 대한 공사조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListServc", "발주계획현황에 대한 용역조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListFrgcpt", "발주계획현황에 대한 외자조회"),
			() -> saveThisYearOrderPlanSttus("getOrderPlanSttusListThng", "발주계획현황에 대한 물품조회")
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
				int totalPage;

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

				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				OrderPlanSttusResponseDto responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);

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
						orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, responseDto.getItems(), requestDto);
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

	private UriComponentsBuilder getUriComponentsBuilder(OrderPlanSttusRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
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
	}
}