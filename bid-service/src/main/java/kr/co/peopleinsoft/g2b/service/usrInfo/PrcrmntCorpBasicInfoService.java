package kr.co.peopleinsoft.g2b.service.usrInfo;

import kr.co.peopleinsoft.g2b.dto.cmmn.BidRequestDto;
import kr.co.peopleinsoft.g2b.dto.userInfo.prcrmntCorp.PrcrmntCorpBasicInfoDto;
import kr.co.peopleinsoft.g2b.dto.userInfo.prcrmntCorp.PrcrmntCorpBasicInfoResponseDto;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BAbstractBidService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

@Service
public class PrcrmntCorpBasicInfoService extends G2BAbstractBidService {

	private final WebClient publicWebClient;

	public PrcrmntCorpBasicInfoService(WebClient publicWebClient) {
		this.publicWebClient = publicWebClient;
	}

	/**
	 * 페이지별 조달업체 기본 정보 수집 / 저장
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
	public <T extends BidRequestDto> int batchInsertPrcrmntCorpBasicInfo(URI uri, int pageNo, T requestDto) throws Exception {
		int rowCnt = 0;

		PrcrmntCorpBasicInfoResponseDto responseDto = publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(PrcrmntCorpBasicInfoResponseDto.class)
			.block();

		if (responseDto == null) {
			throw new Exception("API 호출 실패");
		}

		// api 결과 값의 총 데이터 갯수 및 페이지 계산
		int totalCount = responseDto.getResponse().getBody().getTotalCount();
		int totalPage = (int) Math.ceil((double) totalCount / requestDto.getNumOfRows());

		List<PrcrmntCorpBasicInfoDto> items = responseDto.getResponse().getBody().getItems();

		for (PrcrmntCorpBasicInfoDto item : items) {
			cmmnMapper.insert("PrcrmntCorpBasicInfoMapper.batchInsertPrcrmntCorpBasicInfo", item);
			rowCnt++;
		}

		// 스케줄러 로그기록
		insertSchdulHistLog(uri, pageNo, requestDto, totalPage, totalCount, rowCnt);

		return rowCnt;
	}
}