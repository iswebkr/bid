package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.scsbidListSttus.ScsbidListSttusResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class ScsbidInfoSttsService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public ScsbidInfoSttsService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertScsbidListSttus(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		ScsbidListSttusResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(ScsbidListSttusResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<ScsbidListSttusDto> items = responseDto.getResponse().getBody().getItems();

		for (ScsbidListSttusDto item : items) {
			cmmnMapper.insert("ScsbidListSttusMapper.batchInsertScsbidListSttus", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}