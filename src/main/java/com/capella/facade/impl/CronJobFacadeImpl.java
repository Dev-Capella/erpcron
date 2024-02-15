package com.capella.facade.impl;

import com.capella.facade.CronJobFacade;
import com.capella.service.CronJobAsyncService;
import com.capella.service.cronjob.CronJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronJobFacadeImpl implements CronJobFacade {
    protected final CronJobAsyncService cronJobAsyncService;
    protected final CronJobService cronJobService;
    @Override
    public void run(String code) {
        var cronJobModel = cronJobService.getCronJobModel(code);
        //cronJobAsyncService.run(cronJobModel);
    }
}
