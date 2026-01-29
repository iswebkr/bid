package kr.co.peopleinsoft.g2b.scsbidInfo.service;

import kr.co.peopleinsoft.cmmn.dto.BidRequestDto;
import kr.co.peopleinsoft.cmmn.service.G2BAbstractBidService;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengComptList.OpengComptResultListInfoDto;
import kr.co.peopleinsoft.g2b.scsbidInfo.dto.opengComptList.OpengComptResultListInfoResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class OpengComptResultListInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public OpengComptResultListInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	/**
	 * 페이지별 개찰결과 수집 / 저장
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
	public <T extends BidRequestDto> int batchInsertOpengResultListInfo(URI uri, int pageNo, T requestDto) throws Exception {
		int rowCnt = 0;

		OpengComptResultListInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(OpengComptResultListInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new Exception("API 호출 실패");
		}

		List<OpengComptResultListInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (OpengComptResultListInfoDto item : items) {
			cmmnMapper.insert("OpengComptResultListInfoMapper.batchInsertOpengComptResultListInfo", item);
			rowCnt++;
		}

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, rowCnt);

		return rowCnt;
	}
}