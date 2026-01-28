package kr.co.peopleinsoft.scheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.jobs.cntrctInfo.getCntrctInfoListCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.userInfo.DminsttInfoJob;
import kr.co.peopleinsoft.g2b.jobs.hrcspSsstndrdInfo.HrcspSsstndrdInfoJob;
import kr.co.peopleinsoft.g2b.jobs.scsbidInfo.OpengComptResultListInfoJob;
import kr.co.peopleinsoft.g2b.jobs.scsbidInfo.OpengResultListInfoJob;
import kr.co.peopleinsoft.g2b.jobs.scsbidInfo.OpengResultPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.jobs.orderPlanSttus.OrderPlanSttusJob;
import kr.co.peopleinsoft.g2b.jobs.userInfo.PrcrmntCorpBasicInfoJob;
import kr.co.peopleinsoft.g2b.jobs.scsbidInfo.ScsbidInfoSttsJob;
import kr.co.peopleinsoft.g2b.jobs.bidPublicInfo.getBidPblancListInfoCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.bidPublicInfo.getBidPblancListInfoFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.bidPublicInfo.getBidPblancListInfoServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.bidPublicInfo.getBidPblancListInfoThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.cntrctInfo.getCntrctInfoListFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.cntrctInfo.getCntrctInfoListServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.jobs.cntrctInfo.getCntrctInfoListThngPPSSrchJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@Tag(name = "조달청 자료수집을 위한 Scheduler", description = "입찰정보 수집용 API")
public class BidSchedulerController extends CmmnAbstractController {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public BidSchedulerController(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고공사조회", description = "나라장터검색조건에 의한 입찰공고공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/bidPublicInfo/getBidPblancListInfoCnstwkPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getBidPblancListInfoCnstwkPPSSrch", "bidPublicInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getBidPblancListInfoCnstwkPPSSrchJob.class, "getBidPblancListInfoCnstwkPPSSrch", "bidPublicInfo", "나라장터검색조건에 의한 입찰공고공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고외자조회", description = "나라장터검색조건에 의한 입찰공고외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/bidPublicInfo/getBidPblancListInfoFrgcptPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getBidPblancListInfoFrgcptPPSSrch", "bidPublicInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getBidPblancListInfoFrgcptPPSSrchJob.class, "getBidPblancListInfoFrgcptPPSSrch", "bidPublicInfo", "나라장터검색조건에 의한 입찰공고외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고용역조회", description = "나라장터검색조건에 의한 입찰공고용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/bidPublicInfo/getBidPblancListInfoServcPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getBidPblancListInfoServcPPSSrch", "bidPublicInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getBidPblancListInfoServcPPSSrchJob.class, "getBidPblancListInfoServcPPSSrch", "bidPublicInfo", "나라장터검색조건에 의한 입찰공고용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고물품조회", description = "나라장터검색조건에 의한 입찰공고물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/bidPublicInfo/getBidPblancListInfoThngPPSSrch")
	public ResponseEntity<String> getBidPblancListInfoThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getBidPblancListInfoThngPPSSrch", "bidPublicInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getBidPblancListInfoThngPPSSrchJob.class, "getBidPblancListInfoThngPPSSrch", "bidPublicInfo", "나라장터검색조건에 의한 입찰공고물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "수요기관정보 수집", description = "수요기관 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/usrInfoService/DminsttInfoJob")
	public ResponseEntity<String> dminsttInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("DminsttInfoService", "UsrInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(DminsttInfoJob.class, "DminsttInfoService", "usrInfoService", "사용자정보 - 수요기관 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "조달업체정보 수집", description = "조달업체정보 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/usrInfoService/prcrmntCorpBasicInfoJob")
	public ResponseEntity<String> prcrmntCorpBasicInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("prcrmntCorpBasicInfoJob", "usrInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PrcrmntCorpBasicInfoJob.class, "PrcrmntCorpBasicInfoJob", "usrInfoService", "사용자정보 - 조달업체 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "발주계획정보 수집", description = "발주계획정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/orderPlanSttus/orderPlanSttusJob")
	public ResponseEntity<String> OrderPlanSttusJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("OrderPlanSttusJob", "OrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusJob.class, "OrderPlanSttusJob", "orderPlanSttus", "발주계획정보수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "사전규격정보 수집", description = "사전규격정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/hrcspSsstndrdInfo/hrcspSsstndrdInfoJob")
	public ResponseEntity<String> HrcspSsstndrdInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("HrcspSsstndrdInfoJob", "HrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(HrcspSsstndrdInfoJob.class, "HrcspSsstndrdInfoJob", "hrcspSsstndrdInfo", "사전규격정보수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 개찰완료", description = "낙찰정보 - 개찰결과 개찰완료", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengComptResultListInfo")
	public ResponseEntity<String> opengComptResultListInfo(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("OpengComptResultListInfoJob", "scsbidInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengComptResultListInfoJob.class, "OpengComptResultListInfoJob", "scsbidInfo", "개찰결과 개찰완료", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 개찰결과 정보 수집", description = "나라장터 낙찰정보서비스 - 나라장터 검색조건에 의한 개찰결과 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultListInfoJob")
	public ResponseEntity<String> opengResultListInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("opengResultListInfoJob", "scsbidInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoJob.class, "opengResultListInfoJob", "scsbidInfo", "나라장터 검색조건에 의한 개찰결과 정보 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 예비가격상세 목록 정보 수집", description = "나라장터 낙찰정보서비스 - 개찰결과 예비가격상세 목록 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultPreparPcDetailJob")
	public ResponseEntity<String> opengResultPreparPcDetailJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("opengResultPreparPcDetailJob", "scsbidInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultPreparPcDetailJob.class, "opengResultPreparPcDetailJob", "scsbidInfo", "개찰결과 예비가격상세 목록 정보 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 정보 수집", description = "나라장터 낙찰정보서비스 - 나라장터 검색조건에 의한 낙찰된 목록 현황 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/scsbidInfoSttsJob")
	public ResponseEntity<String> scsbidInfoSttsJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("scsbidInfoSttsJob", "scsbidInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ScsbidInfoSttsJob.class, "scsbidInfoSttsJob", "scsbidInfo", "나라장터 검색조건에 의한 낙찰된 목록 현황 정보 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 공사조회", description = "나라장터검색조건에 의한 계약현황 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/cntrctInfo/getCntrctInfoListCnstwkPPSSrch")
	public ResponseEntity<String> getCntrctInfoListCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 5 * * ?");
		cmmnScheduleManager.deleteJob("getCntrctInfoListCnstwkPPSSrch", "cntrctInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getCntrctInfoListCnstwkPPSSrchJob.class, "getCntrctInfoListCnstwkPPSSrch", "cntrctInfo", "나라장터검색조건에 의한 계약현황 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 용역조회", description = "나라장터검색조건에 의한 계약현황 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/cntrctInfo/getCntrctInfoListServcPPSSrch")
	public ResponseEntity<String> getCntrctInfoListServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 5 * * ?");
		cmmnScheduleManager.deleteJob("getCntrctInfoListServcPPSSrch", "cntrctInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getCntrctInfoListServcPPSSrchJob.class, "getCntrctInfoListServcPPSSrch", "cntrctInfo", "나라장터검색조건에 의한 계약현황 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 외자조회", description = "나라장터검색조건에 의한 계약현황 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/cntrctInfo/getCntrctInfoListFrgcptPPSSrch")
	public ResponseEntity<String> getCntrctInfoListFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 5 * * ?");
		cmmnScheduleManager.deleteJob("getCntrctInfoListFrgcptPPSSrch", "cntrctInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getCntrctInfoListFrgcptPPSSrchJob.class, "getCntrctInfoListFrgcptPPSSrch", "cntrctInfo", "나라장터검색조건에 의한 계약현황 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 물품조회", description = "나라장터검색조건에 의한 계약현황 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/cntrctInfo/getCntrctInfoListThngPPSSrch")
	public ResponseEntity<String> getCntrctInfoListThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 5 * * ?");
		cmmnScheduleManager.deleteJob("getCntrctInfoListThngPPSSrch", "cntrctInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getCntrctInfoListThngPPSSrchJob.class, "getCntrctInfoListThngPPSSrch", "cntrctInfo", "나라장터검색조건에 의한 계약현황 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "모든 Job 실행", description = "모든 Job 실행")
	@GetMapping("/shcduler/jobs/executeAllJobs")
	public ResponseEntity<String> executeAllJobs() throws SchedulerException, JsonProcessingException {
		getBidPblancListInfoCnstwkPPSSrch(null); // 입찰공고-공사
		getBidPblancListInfoFrgcptPPSSrch(null); // 입찰공고-외자
		getBidPblancListInfoServcPPSSrch(null); // 입찰공고-용역
		getBidPblancListInfoThngPPSSrch(null); // 입찰공고-물폼

		getCntrctInfoListCnstwkPPSSrch(null); // 계약현황-공사
		getCntrctInfoListFrgcptPPSSrch(null); // 계약현황-외자
		getCntrctInfoListServcPPSSrch(null); // 계약현황-용역
		getCntrctInfoListThngPPSSrch(null); // 계약현황-물폼

		dminsttInfoJob(null); // 수요기관정보수집
		prcrmntCorpBasicInfoJob(null); // 조달업체정보수집

		OrderPlanSttusJob(null); // 발주계획수집
		HrcspSsstndrdInfoJob(null); // 사전규격수집
		opengComptResultListInfo(null); // 개찰결과-개찰완료
		opengResultListInfoJob(null); // 개찰결과정보
		opengResultPreparPcDetailJob(null); // 예비가격
		scsbidInfoSttsJob(null); // 낙찰목록현황


		return ResponseEntity.ok().body("success");
	}

	@Operation(summary = "Job 목록 조회", description = "등록된 Job 목록 조회")
	@GetMapping("/shcduler/jobs/listAllJobs")
	public ResponseEntity<String> listAllJobs() throws SchedulerException, JsonProcessingException {
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "모든 Job 삭제", description = "등록된 모든 Job 삭제")
	@GetMapping("/shcduler/jobs/removeAllJob")
	public ResponseEntity<String> removeAllJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.removeAllJobs();
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "특정 Job 삭제", description = "선택적 Job 삭제 처리")
	@GetMapping("/shcduler/jobs/deleteJob")
	public ResponseEntity<String> deleteJob(String jobName, String jobGroupName) throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob(jobName, jobGroupName);
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}