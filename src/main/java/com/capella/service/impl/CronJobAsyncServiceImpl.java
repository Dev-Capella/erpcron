package com.capella.service.impl;

import com.capella.cronjob.CronJob;
import com.capella.service.CronJobAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronJobAsyncServiceImpl implements CronJobAsyncService {
    @Override
    @Async
    public void run(String code, CronJob cronJob) {
        if(Objects.nonNull(cronJob)){
            log.info(code + "---------> periyodik görev tetiklendi.");
            cronJob.run();
        }else{
            log.info(code + " periyodik görev tetiklenemedi çünkü periyodik görev bulunamadı.");
        }
    }
}
