package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidColctHistDto;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoRequestDto;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoResponseDto;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service.HrcspSsstndrdInfoService;
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
import java.util.List;

@Controller
@RequestMapping("/g2b/hrcspSsstndrdInfoService")
@Tag(name = "나라장터 사전규격정보서비스", description = "https://www.data.go.kr/data/15129437/openapi.do")
public class HrcspSsstndrdInfoController extends G2BAbstractBidController {

	private static final Logger logger = LoggerFactory.getLogger(HrcspSsstndrdInfoController.class);

	private final HrcspSsstndrdInfoService hrcspSsstndrdInfoService;

	public HrcspSsstndrdInfoController(HrcspSsstndrdInfoService hrcspSsstndrdInfoService) {
		this.hrcspSsstndrdInfoService = hrcspSsstndrdInfoService;
	}

	@Operation(summary = "사전규격 공사 목록 조회")
	@GetMapping("/getBidPblancListInfoCnstwk")
	public ResponseEntity<String> getPublicPrcureThngInfoCnstwk() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoCnstwk", "사전규격 공사 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "사전규격 용역 목록 조회")
	@GetMapping("/getBidPblancListInfoServc")
	public ResponseEntity<String> getPublicPrcureThngInfoServc() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoServc", "사전규격 용역 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "사전규격 외자 목록 조회")
	@GetMapping("/getBidPblancListInfoFrgcpt")
	public ResponseEntity<String> getPublicPrcureThngInfoFrgcpt() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoFrgcpt", "사전규격 외자 목록 조회"), asyncTaskExecutor);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 물품 목록 조회")
	@GetMapping("/getBidPblancListInfoThng")
	public ResponseEntity<String> getPublicPrcureThngInfoThng() {
		return asyncProcess(() -> savePublicPrcureThngInfo("getPublicPrcureThngInfoThng", "입찰공고목록 정보에 대한 물품조회"), asyncTaskExecutor);
	}

	@Operation(summary = "이번년도 나라장터 검색조건에 의한 사전규격 목록 조회")
	@GetMapping("/colctThisYearPublicPrcureThngInfo")
	public ResponseEntity<String> colctThisYearPublicPrcureThngInfo() {
		List<Runnable> colcList = List.of(
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoCnstwk", "사전규격 공사 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoServc", "사전규격 용역 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoFrgcpt", "사전규격 외자 목록 조회"),
			() -> saveThisYearPublicPrcureThngInfo("getPublicPrcureThngInfoThng", "사전규격 물품 목록 조회")
		);
		return asyncParallelProcess(colcList, asyncTaskExecutor);
	}

	private void saveThisYearPublicPrcureThngInfo(String serviceId, String serviceDescription) {
		int thisYear = LocalDateTime.now().getYear();
		int thisMonth = LocalDateTime.now().getMonthValue();
		savePublicPrcureThngInfo(serviceId, serviceDescription, thisYear, thisYear, thisMonth, thisMonth);
	}

	private void savePublicPrcureThngInfo(String serviceId, String serviceDescription) {
		int lastYear = LocalDateTime.now().minusYears(1).getYear();
		savePublicPrcureThngInfo(serviceId, serviceDescription, 2020, lastYear, 1, 12);
	}

	private void savePublicPrcureThngInfo(String serviceId, String serviceDescription, int startYear, int endYear, int startMonth, int endMonth) {
		for (int targetYear = endYear; targetYear >= startYear; targetYear--) {
			for (int targetMonth = endMonth; targetMonth >= startMonth; targetMonth--) {
				YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

				String inqryBgnDt = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")) + "010000";
				String inqryEndDt = yearMonth.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2359";

				int startPage;
				int totalPage;

				HrcspSsstndrdInfoRequestDto requestDto = HrcspSsstndrdInfoRequestDto
					.builder()
					.serviceKey(BidEnum.SERIAL_KEY.getKey())
					.serviceId(serviceId)
					.serviceDescription(serviceDescription)
					.inqryBgnDt(inqryBgnDt)
					.inqryEndDt(inqryEndDt)
					.numOfRows(100)
					.inqryDiv(1)
					.type("json")
					.build();

				// URI 를 빌드하고
				UriComponentsBuilder uriComponentsBuilder = getUriComponentsBuilder(requestDto);
				URI uri = uriComponentsBuilder.build().toUri();

				// URI 호출 결과값을 기반으로
				HrcspSsstndrdInfoResponseDto responseDto = getResponse(HrcspSsstndrdInfoResponseDto.class, uri);

				// 첫페이지 데이터 없으면 이후 작업 진행 없음
				if (responseDto == null) {
					return;
				}

				// 페이지 설정 (이전에 수집된 페이지를 기반으로 startPage 재설정)
				startPage = bidSchdulHistManageService.getStartPage(requestDto);
				totalPage = responseDto.getTotalPage();

				requestDto.setTotalCount(responseDto.getTotalCount());
				requestDto.setTotalPage(responseDto.getTotalPage());

				// 첫 페이지부터 전체 페이지수 만큼 루프를 돌며 데이터 저장
				for (int pageNo = startPage; pageNo <= totalPage; pageNo++) {
					if (pageNo == 1) {
						hrcspSsstndrdInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, responseDto.getItems(), requestDto);
					} else {
						uri = uriComponentsBuilder.cloneBuilder()
							.replaceQueryParam("pageNo", pageNo)
							.build().toUri();

						// 페이지별 uri 호출
						responseDto = getResponse(HrcspSsstndrdInfoResponseDto.class, uri);

						requestDto.setTotalCount(responseDto.getTotalCount());
						requestDto.setTotalPage(responseDto.getTotalPage());

						// 페이지별 URI 호출 결과 전체페이지수 및 전체카운트 업데이트 (중간에 추가된 데이터가 있을 수 있음)
						updateColctPageInfo(requestDto);

						// 페이지별 수집 데이터 저장 (100건씩)
						hrcspSsstndrdInfoService.batchInsertHrcspSsstndrdInfo(uri, pageNo, responseDto.getItems(), requestDto);
					}

					try {
						Thread.sleep(1000 * 30);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private UriComponentsBuilder getUriComponentsBuilder(HrcspSsstndrdInfoRequestDto requestDto) {
		return UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("apis.data.go.kr")
			.pathSegment("1230000/ao/HrcspSsstndrdInfoService", requestDto.getServiceId())
			.queryParam("serviceKey", requestDto.getServiceKey())
			.queryParam("pageNo", 1)
			.queryParam("numOfRows", requestDto.getNumOfRows())
			.queryParam("inqryDiv", requestDto.getInqryDiv())
			.queryParam("type", "json")
			.queryParam("inqryBgnDt", requestDto.getInqryBgnDt())
			.queryParam("inqryEndDt", requestDto.getInqryEndDt());
	}
}