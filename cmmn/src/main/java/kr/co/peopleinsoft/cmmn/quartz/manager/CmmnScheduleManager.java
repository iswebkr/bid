package kr.co.peopleinsoft.cmmn.quartz.manager;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Quartz 를 통해 만들어진 Job 을 관리하기 위한 서비스
 * Service 방식이기 때문에 Controller 를 통해 호출 가능한 형태임
 * - 현재는 ComponentScan 대상이 아니므로 필요시 구현하여 사용할 수 있도록 미니 만들어 둠
 */
@Component
public class CmmnScheduleManager {

	static final Logger logger = LoggerFactory.getLogger(CmmnScheduleManager.class);

	@Autowired
	private Scheduler scheduler;

	static final String jobSuffix = "-job";
	static final String triggerSuffix = "-trigger";

	/**
	 * 즉시실행되는 Job
	 */
	public void createImmediateJob(String strJobClass, String jobName, String groupName, String description, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(getJobClass(strJobClass))
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(false) // 실행 후 자동 삭제 (trigger 삭제되면 동시에 삭제)
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		Trigger trigger = TriggerBuilder.newTrigger()
			.withIdentity(generateTriggerName(jobName), groupName)
			.startNow()
			.build();

		scheduler.scheduleJob(jobDetail, trigger);

		logger.info("Job Class : {}", strJobClass);
		logger.info("Registration Immediate Job : {}.{}", groupName, generateJobName(jobName));
	}

	/**
	 * 반복 실행되는 Job
	 */
	public void createRepeatingJob(String strJobClass, String jobName, String groupName, String description, int intervalSeconds, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(getJobClass(strJobClass))
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(true)
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		// SimpleSchedulerBuilder 설정 (Misfire 정책 : 즉실 실행)
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInSeconds(intervalSeconds)
			.repeatForever()
			.withMisfireHandlingInstructionFireNow();

		SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
			.withIdentity(generateTriggerName(jobName), groupName)
			.withSchedule(simpleScheduleBuilder)
			.startNow() // 즉시 실행
			.build();

		scheduler.scheduleJob(jobDetail, simpleTrigger);

		logger.info("Job Class : {}", strJobClass);
		logger.info("Registration Repeating Job : {}.{} (Interval : {} Seconds)", groupName, generateJobName(jobName), intervalSeconds);
	}

	/**
	 * 일정한 횟수만큼 반복 실행하고 종료하는 Job
	 */
	public void createRepeatCountJob(String strJobClass, String jobName, String groupName, String description, int intervalSeconds, int repeatCount, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(getJobClass(strJobClass))
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(true)
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		// SimpleSchedulerBuilder 설정 (Misfire 정책 : 즉실 실행)
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
			.withIntervalInSeconds(intervalSeconds) // 5초 간격으로
			.withRepeatCount(repeatCount - 1);

		SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
			.withIdentity(generateTriggerName(jobName), groupName)
			.withSchedule(simpleScheduleBuilder) // repeatCount 만큼 실행
			.startNow()
			.build();

		scheduler.scheduleJob(jobDetail, simpleTrigger);

		logger.info("Job Class : {}", strJobClass);
		logger.info("Registration Repeating Count Job : {}.{} (Interval : {} Seconds, Repeat Count : {})", groupName, generateJobName(jobName), intervalSeconds, repeatCount);
	}

	/**
	 * Cron Job
	 */
	public void createCronJob(String strJobClass, String jobName, String groupName, String description, String cronExpression, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(getJobClass(strJobClass))
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(true)
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		if (CronExpression.isValidExpression(cronExpression)) {
			/*
				Cron 예제
				오전 6 시 : CronScheduleBuilder.cronSchedule("0 0 6 * * ?")
				매주 일요일 자정 :  CronScheduleBuilder.cronSchedule("0 0 0 ? * SUN")
				매시간 : CronScheduleBuilder.cronSchedule("0 0 * * * ?")
				평일 오전 9시부터 오후 6시까지 1시간마다 실행 : CronScheduleBuilder.cronSchedule("0 0 9-18 ? * MON-FRI")
				매월 마지막 날 23:59 실행 : CronScheduleBuilder.cronSchedule("0 59 23 L * ?")
				복잡한 스케줄: 매주 월,수,금 오전 8:30과 오후 5:30 실행 : CronScheduleBuilder.cronSchedule("0 30 8,17 ? * MON,WED,FRI")

				CalenderIntervalTrigger - 달력기반
				매월1일실행 : CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMonths(1)
				매 2주마다 실행 (월요일 기준) :
				.startAt(DateBuilder.nextGivenDayOfWeek(null, DateBuilder.MONDAY))
		        .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
		        .withIntervalInWeeks(2))
			 */

			// CronScheduleBuilder 설정 (Misfire 정책 적용 : 즉시 실행)
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();

			CronTrigger cronTrigger = TriggerBuilder.newTrigger()
				.withIdentity(generateJobName(jobName), groupName)
				.withSchedule(cronScheduleBuilder) // Misfire 정책 적용 : 중요한 작업인 경우 즉시 실행
				.startNow()
				.build();

			scheduler.scheduleJob(jobDetail, cronTrigger);

			logger.info("Job Class : {}", strJobClass);
			logger.info("Registration Cron Job : {}.{} (Cron Expression : {})", groupName, generateJobName(jobName), cronExpression);
		} else {
			logger.error("Invalid Cron Expression: {}", cronExpression);
		}
	}

	public void createCronJob(Class<? extends Job> jobClass, String jobName, String groupName, String description, String cronExpression, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(jobClass)
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(true)
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		if (CronExpression.isValidExpression(cronExpression)) {
			/*
				Cron 예제
				오전 6 시 : CronScheduleBuilder.cronSchedule("0 0 6 * * ?")
				매주 일요일 자정 :  CronScheduleBuilder.cronSchedule("0 0 0 ? * SUN")
				매시간 : CronScheduleBuilder.cronSchedule("0 0 * * * ?")
				평일 오전 9시부터 오후 6시까지 1시간마다 실행 : CronScheduleBuilder.cronSchedule("0 0 9-18 ? * MON-FRI")
				매월 마지막 날 23:59 실행 : CronScheduleBuilder.cronSchedule("0 59 23 L * ?")
				복잡한 스케줄: 매주 월,수,금 오전 8:30과 오후 5:30 실행 : CronScheduleBuilder.cronSchedule("0 30 8,17 ? * MON,WED,FRI")

				CalenderIntervalTrigger - 달력기반
				매월1일실행 : CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInMonths(1)
				매 2주마다 실행 (월요일 기준) :
				.startAt(DateBuilder.nextGivenDayOfWeek(null, DateBuilder.MONDAY))
		        .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
		        .withIntervalInWeeks(2))
			 */

			// CronScheduleBuilder 설정 (Misfire 정책 적용 : 즉시 실행)
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();

			CronTrigger cronTrigger = TriggerBuilder.newTrigger()
				.withIdentity(generateJobName(jobName), groupName)
				.withSchedule(cronScheduleBuilder) // Misfire 정책 적용 : 중요한 작업인 경우 즉시 실행
				// .startNow()// 최초실행
				.build();

			scheduler.scheduleJob(jobDetail, cronTrigger);

			logger.info("Job Class : {}", jobClass.getCanonicalName());
			logger.info("Registration Cron Job : {}.{} (Cron Expression : {})", groupName, generateJobName(jobName), cronExpression);
		} else {
			logger.error("Invalid Cron Expression: {}", cronExpression);
		}
	}

	/**
	 * Trigger 없이 Job 만 등록 (상황에 맞는 Trigger 를 등록하여 사용하고자 하는 경우)
	 */
	public void addNewJob(String strJobClass, String jobName, String groupName, String description, Map<String, Object> jobData) throws SchedulerException {
		JobDetail jobDetail = JobBuilder.newJob(getJobClass(strJobClass))
			.withIdentity(generateJobName(jobName), groupName)
			.storeDurably(true) // Trigger 를 별도로 지정하여야 하므로 Trigger 독립적으로 지정
			.withDescription(description)
			.build();

		if (!CollectionUtils.isEmpty(jobData)) {
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.putAll(jobData);
		}

		scheduler.addJob(jobDetail, false);

		logger.info("Add New Job : {}.{}", groupName, generateJobName(jobName));
	}

	/**
	 * 등록된 Job 에 상황에 맞는 Trigger 를 활성화
	 */
	public void activateTrigger(String jobName, String groupName, String cronExcpression) throws SchedulerException {
		JobKey jobKey = new JobKey(generateJobName(jobName), groupName);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		if (CronExpression.isValidExpression(cronExcpression)) {
			Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(generateJobName(jobName), groupName)
				.forJob(jobDetail)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExcpression))
				.build();

			scheduler.scheduleJob(trigger);

			logger.info("Activate Trigger : {}.{} (TriggerKey : {})", groupName, generateJobName(jobName), trigger.getKey());
		} else {
			logger.error("Invalid Cron Expression: {}", cronExcpression);
		}
	}

	/**
	 * Trigger 비활성화
	 */
	public void deactiveTrigger(String jobName, String groupName) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(generateJobName(jobName), groupName);
		scheduler.unscheduleJob(triggerKey);
	}

	/**
	 * 기존 Job Scheduler 정보 변경 적용
	 */
	public void updateJobSchedule(String jobName, String groupName, String cronExpression) throws SchedulerException {
		JobKey jobKey = new JobKey(generateJobName(jobName), groupName);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		// Job 이 Trigger 독립적인 경우만 Job Schedule 정보 변경
		if (jobDetail.isDurable()) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				scheduler.unscheduleJob(trigger.getKey());
			}
			activateTrigger(generateJobName(jobName), groupName, cronExpression);
		} else {
			logger.error("Job is not durable : {}", generateJobName(jobName));
		}
	}

	/**
	 * Job 실행을 멈춤
	 */
	public void pauseJob(String jobName, String groupName) throws SchedulerException {
		JobKey jobKey = new JobKey(generateJobName(jobName), groupName);
		scheduler.pauseJob(jobKey);
		logger.info("Pause Job : {}", generateJobName(jobName));
	}

	/**
	 * 멈춘 Job 의 실행을 재개
	 */
	public void resumeJob(String jobName, String groupName) throws SchedulerException {
		JobKey jobKey = new JobKey(generateJobName(jobName), groupName);
		scheduler.resumeJob(jobKey);
		logger.info("Resume Job : {}", generateJobName(jobName));
	}

	/**
	 * 등록된 Job 삭제
	 */
	public void deleteJob(String jobName, String groupName) throws SchedulerException {
		JobKey jobKey = new JobKey(generateJobName(jobName), groupName);
		boolean result = scheduler.deleteJob(jobKey);

		if (result) {
			logger.info("Delete Job : {}", generateJobName(jobName));
		} else {
			logger.info("Job is Not Found : {}", generateJobName(jobName));
		}
	}

	/**
	 * List All Jobs
	 */
	public void listAllJobs() throws SchedulerException {

		logger.info("================================ [Job List] ================================");
		for (String groupName : scheduler.getJobGroupNames()) {
			logger.info("============================================================================");
			logger.info("Group Name : {}", groupName);
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

				logger.info("============================================================================");
				logger.info("Job : {}.{} (Description : {})", jobKey.getGroup(), jobKey.getName(), jobDetail.getDescription());

				for (Trigger trigger : triggers) {
					logger.info("Trigger : {}, Next execution : {}", trigger.getKey().getName(), trigger.getNextFireTime());
				}
				logger.info("============================================================================");
			}
		}
	}

	/**
	 * Job & Trigger 현황 조회
	 */
	public void printJobStatistics() throws SchedulerException {
		Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup());

		int durableJobsCount = 0;
		int nonDurableJobsCount = 0;

		for (JobKey jobKey : jobKeys) {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			if (jobDetail.isDurable()) {
				durableJobsCount++;
			} else {
				nonDurableJobsCount++;
			}
		}

		logger.info("================================ [Jobs Info] ================================");
		logger.info("Total Jobs Count : {}", jobKeys.size());
		logger.info("Durable Job Count : {}", durableJobsCount);
		logger.info("Non-Durable Job Count : {}", nonDurableJobsCount);
		logger.info("============================== [Triggers Info] ==============================");
		logger.info("Total Triggers Count : {}", triggerKeys.size());
		logger.info("=============================================================================");
	}

	/**
	 * 모든 Job 삭제
	 */
	public void removeAllJobs() throws SchedulerException {
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				scheduler.deleteJob(jobKey);
			}
		}
	}

	Class<? extends Job> getJobClass(String strJobClass) {
		try {
			Class<?> loadedClass = Class.forName(strJobClass);
			return loadedClass.asSubclass(Job.class);
		} catch (ClassNotFoundException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getLocalizedMessage());
			}
		}
		return null;
	}

	String generateJobName(String jobName) {
		return jobName + jobSuffix;
	}

	String generateTriggerName(String jobName) {
		return jobName + triggerSuffix;
	}
}