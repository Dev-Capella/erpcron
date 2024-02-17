package com.capella.facade.impl;

import com.capella.cronjob.CronJob;
import com.capella.facade.CronJobFacade;
import com.capella.service.CronJobAsyncService;
import com.capella.service.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronJobFacadeImpl implements CronJobFacade {
    protected final CronJobAsyncService cronJobAsyncService;
    protected final ApplicationContext context;
    @Override
    public void run(String code) {
        var beanName = String.join(ServiceConstant.UNDERSCORE,code);
        context.getBean(beanName);
        CronJob cronJob = (CronJob) context.getBean(beanName);
        cronJobAsyncService.run(code,cronJob);
    }

}
