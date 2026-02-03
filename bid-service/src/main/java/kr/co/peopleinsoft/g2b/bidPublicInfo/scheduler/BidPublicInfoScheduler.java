package kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.BidPblancListInfoCnstwkJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.BidPblancListInfoFrgcptJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.BidPblancListInfoServcJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.BidPblancListInfoThngJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs.ColctLatestBidPblancListInfoJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@Operation(summary = "입찰공고공사조회", description = "입찰공고공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/BidPblancListInfoCnstwkJob")
	public ResponseEntity<String> BidPblancListInfoCnstwkJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("BidPblancListInfoCnstwkJob", "입찰공고"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(BidPblancListInfoCnstwkJob.class, "BidPblancListInfoCnstwkJob", "입찰공고", "입찰공고공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "입찰공고외자조회", description = "입찰공고외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/BidPblancListInfoFrgcptJob")
	public ResponseEntity<String> BidPblancListInfoFrgcptJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("BidPblancListInfoFrgcptJob", "입찰공고"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(BidPblancListInfoFrgcptJob.class, "BidPblancListInfoFrgcptJob", "입찰공고", "입찰공고외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "입찰공고용역조회", description = "입찰공고용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/BidPblancListInfoServcJob")
	public ResponseEntity<String> BidPblancListInfoServcJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("BidPblancListInfoServcJob", "입찰공고"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(BidPblancListInfoServcJob.class, "BidPblancListInfoServcJob", "입찰공고", "입찰공고용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "입찰공고물품조회", description = "입찰공고물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/BidPblancListInfoThngJob")
	public ResponseEntity<String> BidPblancListInfoThngJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("BidPblancListInfoThngJob", "입찰공고"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(BidPblancListInfoThngJob.class, "BidPblancListInfoThngJob", "입찰공고", "입찰공고물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 입찰공고(최신데이터) *****************************/

	@Operation(summary = "최근 입찰공고 데이터 수집", description = "최근 입찰공고 데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/colctLatestBidPblancListInfoJob")
	public ResponseEntity<String> colctLatestBidPblancListInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestBidPblancListInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestBidPblancListInfoJob.class, "ColctLatestBidPblancListInfoJob", "최신자료수집", "최근 입찰공고 데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}