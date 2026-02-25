package kr.co.peopleinsoft.g2b.orderPlanSttus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusRequestDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.dto.OrderPlanSttusResponseDto;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/g2b/orderPlanSttusService")
@Tag(name = "나라장터 발주계획현황서비스", description = "https://www.data.go.kr/data/15129462/openapi.do")
public class OrderPlanSttusController extends G2BAbstractBidController {

	private static final Logger logger = LoggerFactory.getLogger(OrderPlanSttusController.class);

	private final OrderPlanSttusService orderPlanSttusService;

	public OrderPlanSttusController(OrderPlanSttusService orderPlanSttusService) {
		this.orderPlanSttusService = orderPlanSttusService;
	}

	private Map<String, String> getUriMap() {
		Map<String, String> uriMap = new HashMap<>();
		uriMap.put("getOrderPlanSttusListCnstwk", "발주계획현황에 대한 공사조회");
		uriMap.put("getOrderPlanSttusListServc", "발주계획현황에 대한 용역조회");
		uriMap.put("getOrderPlanSttusListFrgcpt", "발주계획현황에 대한 외자조회");
		uriMap.put("getOrderPlanSttusListThng", "발주계획현황에 대한 물품조회");
		return uriMap;
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
			.queryParam("inqryDiv", requestDto.getInqryDiv())
			.queryParam("orderBgnYm", requestDto.getOrderBgnYm())
			.queryParam("orderEndYm", requestDto.getOrderEndYm())
			.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
			.queryParam("inqryEndDt", requestDto.getInqryEndDt());
	}

	@Operation(summary = "5년전 데이터까지 수집")
	@GetMapping("/collectionLastFiveYearData")
	public ResponseEntity<String> collectionLastFiveYearData() {
		LocalDateTime today = LocalDateTime.now();

		int startYear = 2020;
		int endYear = 2025;
		int startMonth = 1;
		int endMonth = 12;

		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {

			if (targetYear == today.getYear()) {
				endMonth = today.getMonthValue();
			}

			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String orderBgnYm = yearMonth.format(DateTimeFormatter.ofPattern("yyyy")) + "01";
				String orderEndYm = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy")) + "12";
				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				getUriMap().forEach((serviceId, serviceDescription) -> {
					asyncProcess(() -> collectionData(serviceId, serviceDescription, orderBgnYm, orderEndYm, inqryBgnDt, inqryEndDt), asyncTaskExecutor);
				});
			}
		}

		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "어제/오늘 데이터 수집")
	@GetMapping("/collectionTodayAndYesterdayData")
	public ResponseEntity<String> collectionTodayAndYesterdayData() {
		LocalDateTime today = LocalDateTime.now(); // 오늘
		LocalDateTime yesterday = today.minusDays(1); // 어제

		String todayStart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0000";
		String todayEnd = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";
		YearMonth todayMonth = YearMonth.from(today);

		String yesterdayStart = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0000";
		String yesterdayEnd = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";
		YearMonth yesterdayMonth = YearMonth.from(yesterday);

		String todayOrderBgnYm = todayMonth.minusYears(1).format(DateTimeFormatter.ofPattern("yyyy")) + "01";
		String todayOrderEndYm = todayMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy")) + "12";

		String yesterdayOrderBgnYm = yesterdayMonth.format(DateTimeFormatter.ofPattern("yyyy")) + "01";
		String yesterdayOrderEndYm = yesterdayMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy")) + "12";

		List<Runnable> runnables = new ArrayList<>();

		getUriMap().forEach((serviceId, serviceDescription) -> {
			runnables.add(() -> todayCollectionData(serviceId, serviceDescription, todayOrderBgnYm, todayOrderEndYm, todayStart, todayEnd));
			runnables.add(() -> todayCollectionData(serviceId, serviceDescription, yesterdayOrderBgnYm, yesterdayOrderEndYm, yesterdayStart, yesterdayEnd));
		});

		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void todayCollectionData(String serviceId, String serviceDescription, String orderBgnYm, String orderEndYm, String inqryBgnDt, String inqryEndDt) {
		OrderPlanSttusRequestDto requestDto = OrderPlanSttusRequestDto.builder()
			.serviceKey(BidEnum.SERIAL_KEY.getKey())
			.serviceId(serviceId)
			.serviceDescription(serviceDescription)
			.orderBgnYm(orderBgnYm)
			.orderEndYm(orderEndYm)
			.inqryBgnDt(inqryBgnDt)
			.inqryEndDt(inqryEndDt)
			.inqryDiv(1)
			.numOfRows(100)
			.type("json")
			.build();

		try {
			UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
			URI uri = uriComponentsBuilder.build().toUri();

			OrderPlanSttusResponseDto responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);

			if (responseDto == null || responseDto.getTotalCount() <= 0) {
				return;
			}

			int totalPage = responseDto.getTotalPage();

			for (int pageNo = totalPage; pageNo >= 1; pageNo--) {
				if (pageNo > 1) {
					uri = uriComponentsBuilder.cloneBuilder().replaceQueryParam("pageNo", pageNo).build().toUri();
					responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);
				}

				if (responseDto == null || responseDto.getTotalCount() <= 0) {
					break;
				}

				orderPlanSttusService.batchInsertBidOrderPlan(responseDto.getItems());
				Thread.sleep(1000 * 20);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage());
			}
		}
	}

	private void collectionData(String serviceId, String serviceDescription, String orderBgnYm, String orderEndYm, String inqryBgnDt, String inqryEndDt) {
		OrderPlanSttusRequestDto requestDto = OrderPlanSttusRequestDto.builder()
			.serviceKey(BidEnum.SERIAL_KEY.getKey())
			.serviceId(serviceId)
			.serviceDescription(serviceDescription)
			.orderBgnYm(orderBgnYm)
			.orderEndYm(orderEndYm)
			.inqryBgnDt(inqryBgnDt)
			.inqryEndDt(inqryEndDt)
			.inqryDiv(1)
			.numOfRows(100)
			.type("json")
			.build();

		try {
			// 전체페이지와 수집완료페이지가 같고 남은 데이터 컨수가 10건 이하면 완료되었다고 보자.
			boolean colctComplete = bidSchdulHistManageService.getColctCompleteYn(requestDto);

			if (!colctComplete) {
				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				OrderPlanSttusResponseDto responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);

				if (responseDto == null || responseDto.getTotalCount() <= 0) {
					return;
				}

				// 페이지 설정 (이전에 수집된 페이지를 기반으로 startPage 재설정)
				int startPage = bidSchdulHistManageService.getStartPage(requestDto);
				int totalPage = responseDto.getTotalPage();

				requestDto.setTotalCount(responseDto.getTotalCount());
				requestDto.setTotalPage(responseDto.getTotalPage());

				for (int pageNo = startPage; pageNo <= totalPage; pageNo++) {
					if (pageNo == 1) {
						orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder().replaceQueryParam("pageNo", pageNo).build().toUri();
						responseDto = getResponse(OrderPlanSttusResponseDto.class, uri);

						if (responseDto == null || responseDto.getTotalCount() <= 0) {
							break;
						}

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						orderPlanSttusService.batchInsertBidOrderPlan(uri, pageNo, responseDto.getItems(), requestDto);
					}

					Thread.sleep(1000 * 20);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage());
			}
		}
	}
}