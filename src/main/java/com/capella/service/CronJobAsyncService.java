package com.capella.service;

import com.capella.cronjob.CronJob;

public interface CronJobAsyncService {
    void run(String code, CronJob cronJob);
}
