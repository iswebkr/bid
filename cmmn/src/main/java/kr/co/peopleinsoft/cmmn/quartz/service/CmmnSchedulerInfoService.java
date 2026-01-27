package kr.co.peopleinsoft.cmmn.quartz.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("cmmnSchedulerInfoService")
public class CmmnSchedulerInfoService {

	private static final Logger logger = LoggerFactory.getLogger(CmmnSchedulerInfoService.class);

	private final Scheduler scheduler;

	public CmmnSchedulerInfoService(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * 모든 Job과 Trigger 정보를 JSON 문자열로 반환
	 *
	 * @return JSON 형식의 스케줄러 정보
	 * @throws SchedulerException      Quartz 스케줄러 조회 중 오류 발생 시
	 * @throws JsonProcessingException JSON 변환 중 오류 발생 시
	 */
	public String getAllJobsAndTriggersAsJson() throws SchedulerException, JsonProcessingException {
		SchedulerInfo schedulerInfo = collectSchedulerInfo();
		JsonMapper mapper = JsonMapper.builder()
			.findAndAddModules()
			.build();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schedulerInfo);
	}

	/**
	 * 모든 Job과 Trigger 정보를 SchedulerInfo 객체로 반환
	 *
	 * @return SchedulerInfo 객체
	 * @throws SchedulerException Quartz 스케줄러 조회 중 오류 발생 시
	 */
	public SchedulerInfo collectSchedulerInfo() throws SchedulerException {
		logger.info("================================ [Collecting Job List] ================================");

		List<JobGroupInfo> jobGroups = new ArrayList<>();
		int totalJobs = 0;
		int totalTriggers = 0;

		// 모든 Job Group 순회
		for (String groupName : scheduler.getJobGroupNames()) {
			logger.info("Processing Group: {}", groupName);

			List<JobInfo> jobs = new ArrayList<>();

			// Group 내의 모든 Job 순회
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

				totalJobs++;
				totalTriggers += triggers.size();

				// Trigger 정보 수집
				List<TriggerInfo> triggerInfoList = triggers.stream()
					.map(trigger -> buildTriggerInfo(trigger, jobKey))
					.collect(Collectors.toList());

				// Job 정보 생성
				JobInfo jobInfo = JobInfo.builder()
					.jobGroup(jobKey.getGroup())
					.jobName(jobKey.getName())
					.jobClass(jobDetail.getJobClass().getName())
					.description(jobDetail.getDescription())
					.durable(jobDetail.isDurable())
					.shouldRecover(jobDetail.requestsRecovery())
					.jobDataMap(new HashMap<>(jobDetail.getJobDataMap()))
					.triggers(triggerInfoList)
					.build();

				jobs.add(jobInfo);

				logger.info("Collected Job: {}.{} with {} triggers",
					jobKey.getGroup(), jobKey.getName(), triggers.size());
			}

			// Job Group 정보 생성
			JobGroupInfo groupInfo = JobGroupInfo.builder()
				.groupName(groupName)
				.jobs(jobs)
				.build();

			jobGroups.add(groupInfo);
		}

		// 전체 스케줄러 정보 생성
		SchedulerInfo schedulerInfo = SchedulerInfo.builder()
			.schedulerName(scheduler.getSchedulerName())
			.schedulerInstanceId(scheduler.getSchedulerInstanceId())
			.timestamp(LocalDateTime.now())
			.started(scheduler.isStarted())
			.inStandbyMode(scheduler.isInStandbyMode())
			.shutdown(scheduler.isShutdown())
			.totalJobs(totalJobs)
			.totalTriggers(totalTriggers)
			.jobGroups(jobGroups)
			.build();

		logger.info("================================ [Collection Complete] ================================");
		logger.info("Total Jobs: {}, Total Triggers: {}", totalJobs, totalTriggers);

		return schedulerInfo;
	}

	/**
	 * Trigger 정보를 TriggerInfo DTO로 변환
	 *
	 * @param trigger Quartz Trigger 객체
	 * @param jobKey  Job Key
	 * @return TriggerInfo DTO
	 */
	private TriggerInfo buildTriggerInfo(Trigger trigger, JobKey jobKey) {
		try {
			TriggerInfo.TriggerInfoBuilder builder = TriggerInfo.builder()
				.triggerGroup(trigger.getKey().getGroup())
				.triggerName(trigger.getKey().getName())
				.triggerType(trigger.getClass().getSimpleName())
				.state(scheduler.getTriggerState(trigger.getKey()).name())
				.description(trigger.getDescription())
				.previousFireTime(convertToLocalDateTime(trigger.getPreviousFireTime()))
				.nextFireTime(convertToLocalDateTime(trigger.getNextFireTime()))
				.startTime(convertToLocalDateTime(trigger.getStartTime()))
				.endTime(convertToLocalDateTime(trigger.getEndTime()))
				.priority(trigger.getPriority())
				.jobDataMap(new HashMap<>(trigger.getJobDataMap()));

			// CronTrigger인 경우 Cron Expression 추가
			if (trigger instanceof CronTrigger cronTrigger) {
				builder.cronExpression(cronTrigger.getCronExpression());
			}

			// SimpleTrigger인 경우 반복 정보 추가
			if (trigger instanceof SimpleTrigger simpleTrigger) {
				builder.repeatInterval(simpleTrigger.getRepeatInterval())
					.repeatCount(simpleTrigger.getRepeatCount());
			}

			return builder.build();

		} catch (SchedulerException e) {
			logger.error("Error building trigger info for job: {}", jobKey, e);
			return TriggerInfo.builder()
				.triggerGroup(trigger.getKey().getGroup())
				.triggerName(trigger.getKey().getName())
				.triggerType(trigger.getClass().getSimpleName())
				.state("ERROR")
				.build();
		}
	}

	/**
	 * Date를 LocalDateTime으로 변환
	 *
	 * @param date 변환할 Date 객체
	 * @return LocalDateTime 객체 (null인 경우 null 반환)
	 */
	private LocalDateTime convertToLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	/**
	 * Job 그룹 정보를 담는 DTO
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class JobGroupInfo {
		private String groupName;
		private List<JobInfo> jobs;
	}

	/**
	 * Job 상세 정보를 담는 DTO
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class JobInfo {
		private String jobGroup;
		private String jobName;
		private String jobClass;
		private String description;
		private boolean durable;
		private boolean shouldRecover;
		private Map<String, Object> jobDataMap;
		private List<TriggerInfo> triggers;
	}

	/**
	 * Trigger 상세 정보를 담는 DTO
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TriggerInfo {
		private String triggerGroup;
		private String triggerName;
		private String triggerType;
		private String state;
		private String description;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime previousFireTime;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime nextFireTime;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime startTime;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime endTime;

		private Integer priority;
		private String cronExpression;  // CronTrigger인 경우
		private Long repeatInterval;    // SimpleTrigger인 경우
		private Integer repeatCount;    // SimpleTrigger인 경우
		private Map<String, Object> jobDataMap;
	}

	/**
	 * 전체 스케줄러 정보를 담는 DTO
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SchedulerInfo {
		private String schedulerName;
		private String schedulerInstanceId;

		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime timestamp;

		private boolean started;
		private boolean inStandbyMode;
		private boolean shutdown;
		private int totalJobs;
		private int totalTriggers;
		private List<JobGroupInfo> jobGroups;
	}
}