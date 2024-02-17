package com.capella.config;

import com.capella.cronjob.CronJob;
import com.capella.domain.enums.CronJobStatus;
import com.capella.domain.model.cronjob.CronJobModel;
import com.capella.service.constant.ServiceConstant;
import com.capella.service.cronjob.CronJobService;
import com.capella.service.model.ModelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class ScheduleConfig implements SchedulingConfigurer {

    protected final ApplicationContext context;
    protected final CronJobService cronJobService;
    protected final ModelService modelService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduleTaskExecutor());

        var cronJobs = getCronJobs();
        var triggerTaskList = new ArrayList<TriggerTask>();

        if (CollectionUtils.isNotEmpty(cronJobs)) {
            for (CronJobModel cronJobModel : cronJobs) {
                try {
                    context.getBean(String.join(ServiceConstant.UNDERSCORE, cronJobModel.getCode()));
                } catch (BeansException e) {
                    BeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClassName(context.getBean(cronJobModel.getCode()).getClass().getName());
                    beanDefinition.setDependsOn(cronJobModel.getCode());
                    beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
                    ((GenericBeanDefinition) beanDefinition).setBeanClass(context.getBean(cronJobModel.getCode()).getClass());
                    ((GenericBeanDefinition) beanDefinition).setSource(context.getBean(cronJobModel.getCode()));
                    ((AnnotationConfigServletWebServerApplicationContext) context)
                            .registerBeanDefinition(String.join(ServiceConstant.UNDERSCORE, cronJobModel.getCode()), beanDefinition);
                }

                CronJob cronJob = (CronJob) context.getBean(String.join(ServiceConstant.UNDERSCORE, cronJobModel.getCode()));
                cronJob.setCronJobModel(cronJobModel);

                var triggerTask = new TriggerTask(cronJob::run,
                        triggerContext -> {
                            Calendar nextExecutionTime = new GregorianCalendar();
                            nextExecutionTime.setTime(getCronJobNextExecutionTime(cronJob, cronJobModel));
                            return nextExecutionTime.getTime().toInstant();
                        }
                );
                triggerTaskList.add(triggerTask);
            }
            taskRegistrar.setTriggerTasksList(triggerTaskList);
        }
    }

    private Date getCronJobNextExecutionTime(CronJob cronJob, CronJobModel cronJobModel) {
        cronJobModel = cronJobService.getCronJobModel(cronJobModel.getCode());

        if (Objects.equals(cronJobModel.getStatus(), CronJobStatus.STOPPED)) {
            return Objects.isNull(cronJobModel.getNextExecutionDate())
                    ? DateUtils.addYears(new Date(), 1000) : cronJobModel.getNextExecutionDate();
        }

        cronJob.setCronJobModel(cronJobModel);
        CronExpression cronExpression = CronExpression.parse(cronJobModel.getTriggerPattern());
        LocalDateTime nexDateTime = cronExpression.next(LocalDateTime.now());
        Date nextDate = Date.from(nexDateTime.atZone(ZoneId.systemDefault()).toInstant());
        cronJobModel.setNextExecutionDate(nextDate);
        modelService.save(cronJobModel);
        return nextDate;
    }

    private Set<CronJobModel> getCronJobs() {
        return cronJobService.getCronJobModels();
    }

    @Bean
    public Executor scheduleTaskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }
}
