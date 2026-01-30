package kr.co.peopleinsoft.g2b.cntrctInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoDto;
import kr.co.peopleinsoft.g2b.cntrctInfo.dto.CntrctInfoReponseDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class CntrctInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public CntrctInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	/**
	 * 페이지별 사전규격 수집 / 저장
	 *
	 * @param uri        페이지 수집 대상 URI
	 * @param pageNo     페이지 수집 대상 페이지
	 * @param requestDto API 정보가 담긴 RequestDto
	 * @param <T>        BidRequestDto 를 상속받아 구현된 Dto 객체
	 */
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		timeout = 300,
		rollbackFor = Exception.class
	)
	public <T extends BidRequestDto> int batchInsertHrcspSsstndrdInfo(URI uri, int pageNo, T requestDto) {
		int rowCnt = 0;

		CntrctInfoReponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(CntrctInfoReponseDto.class)
			.block();

		if (responseDto == null) {
			throw new RuntimeException("API 호출 실패");
		}

		List<CntrctInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (CntrctInfoDto item : items) {
			cmmnMapper.insert("CntrctInfoMapper.batchInsertCntrctInfo", item);
			rowCnt++;
		}

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}