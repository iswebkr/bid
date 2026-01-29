package kr.co.peopleinsoft.mois.stanOrgCd.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.mois.stanOrgCd.dto.StanOrgCdDto;
import kr.co.peopleinsoft.mois.stanOrgCd.dto.StanOrgCdResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class StanOrgCdService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public StanOrgCdService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	/**
	 * 페이지별 사전규격 수집 / 저장
	 *
	 * @param uri        페이지 수집 대상 URI
	 * @param pageNo     페이지 수집 대상 페이지
	 * @param requestDto API 정보가 담긴 RequestDto
	 */
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		timeout = 300,
		rollbackFor = Exception.class
	)
	public void batchInsertStanOrgCd(URI uri, int pageNo, BidRequestDto requestDto) throws InterruptedException {
		int rowCnt = 0;

		String orgCdJson = publicWebClient.get()
			.uri(uri)
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

		JsonArray jsonArray = jsonObject.getAsJsonArray("StanOrgCd");
		if (jsonArray == null || jsonArray.isEmpty()) {
			return;
		}

		JsonElement rowElement = jsonArray.get(1);
		StanOrgCdResponseDto responseDto = gson.fromJson(rowElement, StanOrgCdResponseDto.class);

		if (responseDto == null || responseDto.getRow() == null || responseDto.getRow().isEmpty()) {
			return;
		}

		for (StanOrgCdDto stanOrgCd : responseDto.getRow()) {
			// 이전에 등록된 기관정보 조회
			StanOrgCdDto prevStanOrgCd = cmmnMapper.selectOne("StanOrgCdMapper.batchSelectStanOrgCd", stanOrgCd);

			// 이전 기관이 조회되지 않으면 신규 입력
			if (prevStanOrgCd == null) {
				cmmnMapper.insert("StanOrgCdMapper.batchInsertStanOrgCd", stanOrgCd);
			} else {
				if ("1".equals(stanOrgCd.getStopSelt())) {
					// 폐지된 기관 삭제처리
					cmmnMapper.delete("StanOrgCdMapper.batchDeleteStanOrgCd", stanOrgCd);
				} else {
					// 변경일자가 이전데이터와 다르면 업데이트 진행
					if (!prevStanOrgCd.getChgDe().equals(stanOrgCd.getChgDe())) {
						cmmnMapper.insert("StanOrgCdMapper.batchUpdateStanOrgCd", stanOrgCd);
					}
				}
			}
			rowCnt++;
		}

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);
	}
}