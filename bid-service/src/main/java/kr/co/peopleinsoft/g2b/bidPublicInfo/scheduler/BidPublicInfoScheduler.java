package kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.CollectionLastFiveYearBidInfoJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.CollectionTodayAndYesterDayBidInfoJob;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/bidPublicInfoService")
@Tag(
	name = "나라장터 입찰공고정보서비스(BidPublicInfoService)"
	, description = "조달청의 나라장터에서 제공하는 물품, 용역, 공사, 외자 입찰공고목록, 입찰공고상세정보, 기초금액정보, 면허제한정보, 참가가능지역정보, 입찰공고 변경이력를 제공하며 나라장터 입찰공고 검색조건으로도 업무별 입찰공고 정보를 제공하는 나라장터 입찰공고정보서비스\n" +
	". 조달청과 연계기관의 입찰공고 정보 또한 제공")
public class BidPublicInfoScheduler extends CmmnAbstractController {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public BidPublicInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "최근 5년치 데이터 수집 (매일 저녁 9시)", description = "최근 5년치 데이터 수집")
	@GetMapping("/CollectionLastFiveYearBidInfoJob")
	public ResponseEntity<String> CollectionLastFiveYearBidInfoJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionLastFiveYearBidInfoJob", "입찰공고"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionLastFiveYearBidInfoJob.class, "CollectionLastFiveYearBidInfoJob", "입찰공고", "최근 5년치 데이터 수집", "0 0 21 * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "어제,오늘 데이터만 수집 (5분 단위 수집)", description = "최근 입찰공고 데이터 수집")
	@GetMapping("/CollectionTodayAndYesterDayBidInfoJob")
	public ResponseEntity<String> CollectionTodayAndYesterDayBidInfoJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionTodayAndYesterDayBidInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionTodayAndYesterDayBidInfoJob.class, "CollectionTodayAndYesterDayBidInfoJob", "최신자료수집", "최근 입찰공고 데이터 수집", "0 */5 * * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}