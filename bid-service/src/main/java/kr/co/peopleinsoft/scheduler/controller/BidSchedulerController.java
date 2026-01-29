package kr.co.peopleinsoft.scheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.job.getBidPblancListInfoCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.job.getBidPblancListInfoFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.job.getBidPblancListInfoServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.bidPublicInfo.job.getBidPblancListInfoThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.job.getCntrctInfoListCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.job.getCntrctInfoListFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.job.getCntrctInfoListServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.job.getCntrctInfoListThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.job.getPublicPrcureThngInfoCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.job.getPublicPrcureThngInfoFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.job.getPublicPrcureThngInfoServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.job.getPublicPrcureThngInfoThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.job.getOrderPlanSttusListCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.job.getOrderPlanSttusListFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.job.getOrderPlanSttusListServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.job.getOrderPlanSttusListThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultListInfo.getOpengResultListInfoCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultListInfo.getOpengResultListInfoFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultListInfo.getOpengResultListInfoServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultListInfo.getOpengResultListInfoThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultPreparPcDetail.getOpengResultListInfoCnstwkPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultPreparPcDetail.getOpengResultListInfoFrgcptPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultPreparPcDetail.getOpengResultListInfoServcPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultPreparPcDetail.getOpengResultListInfoThngPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.ppengComptResultListInfo.OpengComptResultListInfoJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.scsbidInfoStts.getScsbidListSttusCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.scsbidInfoStts.getScsbidListSttusFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.scsbidInfoStts.getScsbidListSttusServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.job.scsbidInfoStts.getScsbidListSttusThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.userInfo.job.DminsttInfoJob;
import kr.co.peopleinsoft.g2b.userInfo.job.PrcrmntCorpBasicInfoJob;
import kr.co.peopleinsoft.mois.stanOrgCd.job.getStanOrgCdList2Job;
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
	@GetMapping("/shcduler/usrInfoService/dminsttInfo")
	public ResponseEntity<String> dminsttInfo(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("dminsttInfo", "UsrInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(DminsttInfoJob.class, "dminsttInfo", "usrInfoService", "사용자정보 - 수요기관 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "조달업체정보 수집", description = "조달업체정보 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/usrInfoService/prcrmntCorpBasicInfo")
	public ResponseEntity<String> prcrmntCorpBasicInfo(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("prcrmntCorpBasicInfo", "usrInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PrcrmntCorpBasicInfoJob.class, "prcrmntCorpBasicInfo", "usrInfoService", "사용자정보 - 조달업체 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 발주계획 ***/

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/orderPlanSttus/getOrderPlanSttusListCnstwkPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("getOrderPlanSttusListCnstwkPPSSrch", "OrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOrderPlanSttusListCnstwkPPSSrchJob.class, "getOrderPlanSttusListCnstwkPPSSrch", "orderPlanSttus", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/orderPlanSttus/getOrderPlanSttusListServcPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("getOrderPlanSttusListServcPPSSrch", "OrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOrderPlanSttusListServcPPSSrchJob.class, "getOrderPlanSttusListServcPPSSrch", "orderPlanSttus", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/orderPlanSttus/getOrderPlanSttusListFrgcptPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("getOrderPlanSttusListFrgcptPPSSrch", "OrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOrderPlanSttusListFrgcptPPSSrchJob.class, "getOrderPlanSttusListFrgcptPPSSrch", "orderPlanSttus", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/orderPlanSttus/getOrderPlanSttusListThngPPSSrch")
	public ResponseEntity<String> getOrderPlanSttusListThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("getOrderPlanSttusListThngPPSSrch", "OrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOrderPlanSttusListThngPPSSrchJob.class, "getOrderPlanSttusListThngPPSSrch", "orderPlanSttus", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 사전규격 ***/

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 공사 목록 조회", description = "나라장터 검색조건에 의한 사전규격 공사 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/hrcspSsstndrdInfo/getPublicPrcureThngInfoCnstwkPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getPublicPrcureThngInfoCnstwkPPSSrch", "HrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getPublicPrcureThngInfoCnstwkPPSSrchJob.class, "getPublicPrcureThngInfoCnstwkPPSSrch", "hrcspSsstndrdInfo", "나라장터 검색조건에 의한 사전규격 공사 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 용역 목록 조회", description = "나라장터 검색조건에 의한 사전규격 용역 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/hrcspSsstndrdInfo/getPublicPrcureThngInfoServcPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getPublicPrcureThngInfoServcPPSSrch", "HrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getPublicPrcureThngInfoServcPPSSrchJob.class, "getPublicPrcureThngInfoServcPPSSrch", "hrcspSsstndrdInfo", "나라장터 검색조건에 의한 사전규격 용역 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 외자 목록 조회", description = "나라장터 검색조건에 의한 사전규격 외자 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/hrcspSsstndrdInfo/getPublicPrcureThngInfoFrgcptPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getPublicPrcureThngInfoFrgcptPPSSrch", "HrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getPublicPrcureThngInfoFrgcptPPSSrchJob.class, "getPublicPrcureThngInfoFrgcptPPSSrch", "hrcspSsstndrdInfo", "나라장터 검색조건에 의한 사전규격 외자 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 사전규격 물품 목록 조회", description = "나라장터 검색조건에 의한 사전규격 물품 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/hrcspSsstndrdInfo/getPublicPrcureThngInfoThngPPSSrch")
	public ResponseEntity<String> getPublicPrcureThngInfoThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("getPublicPrcureThngInfoThngPPSSrch", "HrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getPublicPrcureThngInfoThngPPSSrchJob.class, "getPublicPrcureThngInfoThngPPSSrch", "hrcspSsstndrdInfo", "나라장터 검색조건에 의한 사전규격 물품 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 낙찰정보 - 개찰결과 - 개찰완료 ***/

	@Operation(summary = "낙찰정보 - 개찰결과 개찰완료", description = "낙찰정보 - 개찰결과 개찰완료", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/ppengComptResultListInfo/OpengComptResultListInfo")
	public ResponseEntity<String> opengComptResultListInfo(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("opengComptResultListInfo", "scsbidInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengComptResultListInfoJob.class, "opengComptResultListInfo", "ppengComptResultListInfo", "개찰결과 개찰완료", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 낙찰정보 - 개찰결과 ***/

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 공사 목록 조회", description = "나라장터 검색조건에 의한 개찰결과 공사 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultListInfo/getOpengResultListInfoCnstwkPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoCnstwkPPSSrch", "opengResultListInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoCnstwkPPSSrchJob.class, "getOpengResultListInfoCnstwkPPSSrch", "opengResultListInfo", "나라장터 검색조건에 의한 개찰결과 공사 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 공사 용역 조회", description = "나라장터 검색조건에 의한 개찰결과 용역 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultListInfo/getOpengResultListInfoServcPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoServcPPSSrch", "opengResultListInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoServcPPSSrchJob.class, "getOpengResultListInfoServcPPSSrch", "opengResultListInfo", "나라장터 검색조건에 의한 개찰결과 용역 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 공사 외자 조회", description = "나라장터 검색조건에 의한 개찰결과 외자 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultListInfo/getOpengResultListInfoFrgcptPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoFrgcptPPSSrch", "opengResultListInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoFrgcptPPSSrchJob.class, "getOpengResultListInfoFrgcptPPSSrch", "opengResultListInfo", "나라장터 검색조건에 의한 개찰결과 외자 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 개찰결과 공사 물품 조회", description = "나라장터 검색조건에 의한 개찰결과 물품 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultListInfo/getOpengResultListInfoThngPPSSrch")
	public ResponseEntity<String> getOpengResultListInfoThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoThngPPSSrch", "opengResultListInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoThngPPSSrchJob.class, "getOpengResultListInfoThngPPSSrch", "opengResultListInfo", "나라장터 검색조건에 의한 개찰결과 물품 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 낙찰정보 - 예비가격 ***/

	@Operation(summary = "낙찰정보 - 개찰결과 공사 예비가격상세 목록 조회", description = "개찰결과 공사 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultPreparPcDetail/getOpengResultListInfoCnstwkPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoCnstwkPreparPcDetail(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoCnstwkPreparPcDetail", "opengResultPreparPcDetail"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoCnstwkPreparPcDetailJob.class, "getOpengResultListInfoCnstwkPreparPcDetail", "opengResultPreparPcDetail", "개찰결과 공사 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 용역 예비가격상세 목록 조회", description = "개찰결과 용역 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultPreparPcDetail/getOpengResultListInfoServcPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoServcPreparPcDetail(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoServcPreparPcDetail", "opengResultPreparPcDetail"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoServcPreparPcDetailJob.class, "getOpengResultListInfoServcPreparPcDetail", "opengResultPreparPcDetail", "개찰결과 용역 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 외자 예비가격상세 목록 조회", description = "개찰결과 외자 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultPreparPcDetail/getOpengResultListInfoFrgcptPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoFrgcptPreparPcDetail(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoFrgcptPreparPcDetail", "opengResultPreparPcDetail"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoFrgcptPreparPcDetailJob.class, "getOpengResultListInfoFrgcptPreparPcDetail", "opengResultPreparPcDetail", "개찰결과 외자 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 물품 예비가격상세 목록 조회", description = "개찰결과 물품 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/opengResultPreparPcDetail/getOpengResultListInfoThngPreparPcDetail")
	public ResponseEntity<String> getOpengResultListInfoThngPreparPcDetail(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("getOpengResultListInfoThngPreparPcDetail", "opengResultPreparPcDetail"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getOpengResultListInfoThngPreparPcDetailJob.class, "getOpengResultListInfoThngPreparPcDetail", "opengResultPreparPcDetail", "개찰결과 물품 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 낙찰목록 ***/

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/scsbidInfoStts/getScsbidListSttusCnstwkPPSSrch")
	public ResponseEntity<String> getScsbidListSttusCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("getScsbidListSttusCnstwkPPSSrch", "scsbidInfoStts"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getScsbidListSttusCnstwkPPSSrchJob.class, "getScsbidListSttusCnstwkPPSSrch", "scsbidInfoStts", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/scsbidInfoStts/getScsbidListSttusServcPPSSrch")
	public ResponseEntity<String> getScsbidListSttusServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("getScsbidListSttusServcPPSSrch", "scsbidInfoStts"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getScsbidListSttusServcPPSSrchJob.class, "getScsbidListSttusServcPPSSrch", "scsbidInfoStts", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/scsbidInfoStts/getScsbidListSttusFrgcptPPSSrch")
	public ResponseEntity<String> getScsbidListSttusFrgcptPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("getScsbidListSttusFrgcptPPSSrch", "scsbidInfoStts"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getScsbidListSttusFrgcptPPSSrchJob.class, "getScsbidListSttusFrgcptPPSSrch", "scsbidInfoStts", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/scsbidInfo/scsbidInfoStts/getScsbidListSttusThngPPSSrch")
	public ResponseEntity<String> getScsbidListSttusThngPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("getScsbidListSttusThngPPSSrch", "scsbidInfoStts"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getScsbidListSttusThngPPSSrchJob.class, "getScsbidListSttusThngPPSSrch", "scsbidInfoStts", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/*** 계약현황 ***/

	@Operation(summary = "나라장터검색조건에 의한 계약현황 공사조회", description = "나라장터검색조건에 의한 계약현황 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/cntrctInfo/getCntrctInfoListCnstwkPPSSrch")
	public ResponseEntity<String> getCntrctInfoListCnstwkPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
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
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
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
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
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
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("getCntrctInfoListThngPPSSrch", "cntrctInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getCntrctInfoListThngPPSSrchJob.class, "getCntrctInfoListThngPPSSrch", "cntrctInfo", "나라장터검색조건에 의한 계약현황 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "행정안전부_행정표준코드_기관코드", description = "행정안전부_행정표준코드_기관코드", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/mois/getStanOrgCdList2")
	public ResponseEntity<String> getStanOrgCdList2(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 30 18 * * ?");
		cmmnScheduleManager.deleteJob("getStanOrgCdList2", "mois"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(getStanOrgCdList2Job.class, "getStanOrgCdList2", "mois", "행정안전부_행정표준코드_기관코드", cronJobExpression, new HashMap<>());
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

		getOrderPlanSttusListCnstwkPPSSrch(null); // 발주계획(공사)
		getOrderPlanSttusListServcPPSSrch(null); // 발주계획(용역)
		getOrderPlanSttusListFrgcptPPSSrch(null); // 발주계획(외자)
		getOrderPlanSttusListThngPPSSrch(null); // 발주계획(물품)

		getPublicPrcureThngInfoCnstwkPPSSrch(null); // 사전규격(공사)
		getPublicPrcureThngInfoServcPPSSrch(null); // 사전규격(용역)
		getPublicPrcureThngInfoFrgcptPPSSrch(null); // 사전규격(외자)
		getPublicPrcureThngInfoThngPPSSrch(null); // 사전규격(물품)

		getOpengResultListInfoCnstwkPPSSrch(null); // 낙찰-개찰결과정보(공사)
		getOpengResultListInfoServcPPSSrch(null); // 낙찰-개찰결과정보(용역)
		getOpengResultListInfoFrgcptPPSSrch(null); // 낙찰-개찰결과정보(외자)
		getOpengResultListInfoThngPPSSrch(null); // 낙찰-개찰결과정보(물품)

		opengComptResultListInfo(null); // 낙찰-개찰결과-개찰완료

		getOpengResultListInfoCnstwkPreparPcDetail(null); // 낙찰-예비가격(공사)
		getOpengResultListInfoServcPreparPcDetail(null); // 낙찰-예비가격(용역)
		getOpengResultListInfoFrgcptPreparPcDetail(null); // 낙찰-예비가격(외자)
		getOpengResultListInfoThngPreparPcDetail(null); // 낙찰-예비가격(물품)

		getScsbidListSttusCnstwkPPSSrch(null); // 낙찰-낙찰목록(공사)
		getScsbidListSttusServcPPSSrch(null); // 낙찰-예비가격(용역)
		getScsbidListSttusFrgcptPPSSrch(null); // 낙찰-예비가격(외자)
		getScsbidListSttusThngPPSSrch(null); // 낙찰-예비가격(물품)

		dminsttInfo(null); // 사용자-수요기관정보수집
		prcrmntCorpBasicInfo(null); // 사용자-조달업체정보수집

		getStanOrgCdList2(null); // 행정안전부 기관코드 수집

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