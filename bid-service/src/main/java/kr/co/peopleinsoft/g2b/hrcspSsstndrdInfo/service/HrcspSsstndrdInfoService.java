package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service;


import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoDto;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.dto.HrcspSsstndrdInfoResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class HrcspSsstndrdInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public HrcspSsstndrdInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	public <T extends BidRequestDto> int batchInsertHrcspSsstndrdInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		HrcspSsstndrdInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(HrcspSsstndrdInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<HrcspSsstndrdInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (HrcspSsstndrdInfoDto item : items) {
			cmmnMapper.insert("BidHrcspSsstndrdInfoMapper.batchInsertHrcspSsstndrdInfo", item);
		}

		rowCnt = cmmnMapper.flushBatchStatementsCount();

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}