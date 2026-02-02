package kr.co.peopleinsoft.g2b.bidPublicInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoDto;
import kr.co.peopleinsoft.g2b.bidPublicInfo.dto.BidPublicInfoResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class BidPublicInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public BidPublicInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertPublicInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt;

		BidPublicInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(BidPublicInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<BidPublicInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (BidPublicInfoDto item : items) {
			cmmnMapper.insert("BidPublicInfoMapper.batchInsertPublicInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}