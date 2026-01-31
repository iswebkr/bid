package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList.OpengResultListInfoDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultList.OpengResultListInfoResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class OpengResultListInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public OpengResultListInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertOpengResultListInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		OpengResultListInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(OpengResultListInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<OpengResultListInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (OpengResultListInfoDto item : items) {
			cmmnMapper.insert("OpengResultListInfoMapper.batchInsertOpengResultListInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}