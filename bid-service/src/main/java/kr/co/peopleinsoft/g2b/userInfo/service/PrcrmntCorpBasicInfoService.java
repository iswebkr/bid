package kr.co.peopleinsoft.g2b.userInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoDto;
import kr.co.peopleinsoft.g2b.userInfo.dto.prcrmntCorp.PrcrmntCorpBasicInfoResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class PrcrmntCorpBasicInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public PrcrmntCorpBasicInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertPrcrmntCorpBasicInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		PrcrmntCorpBasicInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(PrcrmntCorpBasicInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<PrcrmntCorpBasicInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (PrcrmntCorpBasicInfoDto item : items) {
			cmmnMapper.insert("PrcrmntCorpBasicInfoMapper.batchInsertPrcrmntCorpBasicInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}