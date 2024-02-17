package com.capella.cronjob.impl;

import com.capella.cronjob.CronJob;
import com.capella.domain.enums.CronJobStatus;
import com.capella.domain.model.cronjoblog.CronJobLogModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ExchangeCronJob extends CronJob {
    public final static String CRONJOB_NAME = "exchange_recorder";

    @Value("${tcmb.exchange.xml}")
    private String exchangeXml;

    @Override
    public void run() {
        super.run();

        this.cronJobModel = cronJobService.getCronJobModel(cronJobModel.getCode());
        var cronJobLogModel = modelService.create(CronJobLogModel.class);
        cronJobLogModel.setCode(UUID.randomUUID().toString());
        var startDate = LocalDateTime.now();
        try {
            if(!CronJobStatus.STOPPED.equals(cronJobModel.getStatus())){
                log.info(CRONJOB_NAME + " job başladı");
                saveCronJobLog(cronJobModel, cronJobLogModel, CronJobStatus.RUNNING, JOB_START);
                cronJobModel.setStatus(CronJobStatus.RUNNING);
                modelService.save(cronJobModel);

                //cron job işlemi

                saveCronJobLog(cronJobModel, cronJobLogModel, CronJobStatus.SUCCESSFUL, JOB_END);
                cronJobModel.setStatus(CronJobStatus.SUCCESSFUL);
                modelService.save(cronJobModel);
                log.info(CRONJOB_NAME + " job başarıyla tamalandı");
            }else{
                log.info(CRONJOB_NAME + " durumu DURDURULDU olan bir job başlayamaz.");
            }
        }catch (Throwable e){
            cronJobModel.setStatus(CronJobStatus.FAILED);
            modelService.save(cronJobModel);
        }
    }
}
