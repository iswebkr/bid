package kr.co.peopleinsoft.mois.stanOrgCd.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.controller.G2BAbstractBidController;
import kr.co.peopleinsoft.cmmn.dto.BidEnum;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.mois.stanOrgCd.dto.StanOrgCdHeadDto;
import kr.co.peopleinsoft.mois.stanOrgCd.service.StanOrgCdService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/mois")
@Tag(name = "행정안전부 행정표준기관코드", description = "https://www.data.go.kr/data/15129427/openapi.do")
public class StanOrgCdController extends G2BAbstractBidController {

	private final StanOrgCdService stanOrgCdService;

	public StanOrgCdController(StanOrgCdService stanOrgCdService) {
		this.stanOrgCdService = stanOrgCdService;
	}

	@Operation(summary = "행정표준기관코드 수집")
	@GetMapping("/getStanOrgCdList2")
	public ResponseEntity<String> getStanOrgCdList2() {
		return asyncProcess(() -> saveStanOrgCdList("getStanOrgCdList2", "행정안전부_행정표준코드_기관코드"), asyncTaskExecutor);
	}

	public void saveStanOrgCdList(String serviceId, String serviceDescription) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		BidRequestDto requestDto = BidRequestDto.builder()
			.serviceKey(BidEnum.SERIAL_KEY.getKey())
			.serviceId(serviceId)
			.serviceDescription(serviceDescription)
			.inqryBgnDt(date)
			.inqryEndDt(date)
			.numOfRows(100)
			.type("json")
			.build();

		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("apis.data.go.kr")
			.pathSegment("1741000/StanOrgCd2", requestDto.getServiceId())
			.queryParam("serviceKey", requestDto.getServiceKey())
			.queryParam("pageNo", 1)
			.queryParam("numOfRows", requestDto.getNumOfRows())
			.queryParam("type", requestDto.getType());

		URI getStanOrgCdListUri = uriComponentsBuilder.build().toUri();

		String orgCdJson = publicWebClient.get()
			.uri(getStanOrgCdListUri)
			.retrieve()
			.bodyToMono(String.class)
			.block();

		Gson gson = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

		JsonObject jsonObject = gson.fromJson(orgCdJson, JsonObject.class);

		if (jsonObject == null) {
			return;
		}

		JsonArray stanOrgCdList = jsonObject.getAsJsonArray("StanOrgCd");
		if (stanOrgCdList == null || stanOrgCdList.isEmpty()) {
			return;
		}

		JsonElement headElement = stanOrgCdList.get(0);
		StanOrgCdHeadDto headDto = gson.fromJson(headElement, StanOrgCdHeadDto.class);

		if (headDto == null || headDto.getHead() == null || headDto.getHead().isEmpty()) {
			return;
		}

		Map<String, Object> firstHead = headDto.getHead().getFirst();
		if (firstHead == null || !firstHead.containsKey("totalCount")) {
			return;
		}

		Object totalCountObject = firstHead.get("totalCount");
		int totalCount = 0;

		if (totalCountObject instanceof Number) {
			totalCount = ((Number) totalCountObject).intValue();
		} else if (totalCountObject instanceof String) {
			totalCount = Integer.parseInt((String) totalCountObject);
		} else {
			return;
		}

		int startPage;
		int endPage;
		String totalCountIsNotMatch;

		int totalPage = (int) Math.ceil((double) totalCount / 100);

		requestDto.setTotalPage(totalPage);
		requestDto.setTotalCount(totalCount);

		//Map<String, Object> pageMap = g2BCmmnService.initPageCorrection(requestDto);
		Map<String, Object> pageMap = new HashMap<>();

		startPage = (Integer) pageMap.get("startPage");
		endPage = (Integer) pageMap.get("endPage");
		totalCountIsNotMatch = (String) pageMap.get("totalCountIsNotMatch");

		// 전체 카운트가 변한 경우 1페이지 부터 다시 받음
		if ("Y".equals(totalCountIsNotMatch)) {
			startPage = 1;
		}

		for (int pageNo = startPage; pageNo <= endPage; pageNo++) {
			URI uri = uriComponentsBuilder.cloneBuilder()
				.replaceQueryParam("pageNo", pageNo)
				.build().toUri();
			stanOrgCdService.batchInsertStanOrgCd(uri, pageNo, requestDto);

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