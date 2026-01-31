package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultPreparPcDetail.OpengResultPreparPcDetailDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengResultPreparPcDetail.OpengResultPreparPcDetailResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class OpengResultPreparPcDetailService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public OpengResultPreparPcDetailService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertOpengResultPreparPcDetailInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		OpengResultPreparPcDetailResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(OpengResultPreparPcDetailResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<OpengResultPreparPcDetailDto> items = responseDto.getResponse().getBody().getItems();

		for (OpengResultPreparPcDetailDto item : items) {
			cmmnMapper.insert("OpengResultListInfoPreparPcDetailMapper.batchInsertOpengResultListInfoPreparPcDetail", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}