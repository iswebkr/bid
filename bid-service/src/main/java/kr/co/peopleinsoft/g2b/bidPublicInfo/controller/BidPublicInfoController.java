package kr.co.peopleinsoft.g2b.bidPublicInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoRequestDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoResponseDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
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
@RequestMapping("/g2b/bidPublicInfoService")
@Tag(name = "조달청_나라장터 입찰공고정보서비스", description = "https://www.data.go.kr/data/15129394/openapi.do")
public class BidPublicInfoController extends CmmnAbstractController {

	private final G2BCmmnService g2BCmmnService;
	private final AsyncTaskExecutor asyncTaskExecutor;
	private final WebClient publicWebClient;
	private final BidPublicInfoService bidPublicInfoService;

	public BidPublicInfoController(G2BCmmnService g2BCmmnService, AsyncTaskExecutor asyncTaskExecutor, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService) {
		this.g2BCmmnService = g2BCmmnService;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.publicWebClient = publicWebClient;
		this.bidPublicInfoService = bidPublicInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고공사조회")
	@GetMapping("/getBidPblancListInfoCnstwkPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoCnstwkPPSSrch() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoCnstwkPPSSrch", "공사", "나라장터검색조건에 의한 입찰공고공사조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고외자조회")
	@GetMapping("/getBidPblancListInfoFrgcptPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoFrgcptPPSSrch() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoFrgcptPPSSrch", "외자", "나라장터검색조건에 의한 입찰공고외자조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고용역조회")
	@GetMapping("/getBidPblancListInfoServcPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoServcPPSSrch() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoServcPPSSrch", "용역", "나라장터검색조건에 의한 입찰공고용역조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고물품조회")
	@GetMapping("/getBidPblancListInfoThngPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoThngPPSSrch() {
		return asyncProcess(() -> saveBidPblancListInfo("getBidPblancListInfoThngPPSSrch", "물품", "나라장터검색조건에 의한 입찰공고물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "입찰공고 이번 년도 데이터만 조회")
	@GetMapping("/colctThisYearBidPblancListInfo")
	public ResponseEntity<String> colctThisYearBidPblancListInfo() {
		List<Runnable> runnables = List.of(
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoCnstwkPPSSrch", "공사", "나라장터검색조건에 의한 입찰공고공사조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoServcPPSSrch", "용역", "나라장터검색조건에 의한 입찰공고용역조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoFrgcptPPSSrch", "외자", "나라장터검색조건에 의한 입찰공고외자조회"),
			() -> saveThisYearBidPblancListInfo("getBidPblancListInfoThngPPSSrch", "물품", "나라장터검색조건에 의한 입찰공고물품조회")
		);
		return asyncParallelProcess(runnables, asyncTaskExecutor);
	}

	private void saveThisYearBidPblancListInfo(String serviceId, String bidType, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		saveBidPblancListInfo(serviceId, bidType, serviceDescription, thisYear, thisYear, 1, thisMonth);
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		saveBidPblancListInfo(serviceId, bidType, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void saveBidPblancListInfo(String serviceId, String bidType, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
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
					.bidType(bidType)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.inqryDiv(1)
					.type("json")
					.build();

				UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
					.scheme("https")
					.host("apis.data.go.kr")
					.pathSegment("1230000/ad/BidPublicInfoService", requestDto.getServiceId())
					.queryParam("serviceKey", requestDto.getServiceKey())
					.queryParam("pageNo", 1)
					.queryParam("numOfRows", requestDto.getNumOfRows())
					.queryParam("inqryDiv", requestDto.getInqryDiv())
					.queryParam("type", "json")
					.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
					.queryParam("inqryEndDt", requestDto.getInqryEndDt());

				URI firstPageUri = uriComponentsBuilder.build().toUri();

				// 1 페이지 API 호출
				BidPublicInfoResponseDto responseDto = publicWebClient.get()
					.uri(firstPageUri)
					.retrieve()
					.bodyToMono(BidPublicInfoResponseDto.class)
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
					bidPublicInfoService.batchInsertPublicInfo(uri, pageNo, requestDto);

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