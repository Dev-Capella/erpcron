package com.capella.cronjob;

import com.capella.domain.data.user.JwtUserData;
import com.capella.domain.enums.CronJobStatus;
import com.capella.domain.model.cronjob.CronJobModel;
import com.capella.domain.model.cronjoblog.CronJobLogModel;
import com.capella.security.constant.AuthorizationConstants;
import com.capella.service.cronjob.CronJobService;
import com.capella.service.model.ModelService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@Getter
public class CronJob implements Runnable{

    @Autowired
    protected CronJobService cronJobService;
    @Autowired
    protected ModelService modelService;

    @Getter
    @Setter
    protected CronJobModel cronJobModel;

    public final static String CRONJOB_USER = "cronjob_user";
    public final static String JOB_START = "Job başladı";
    public final static String JOB_END = "Job bitti";

    @Override
    public void run() {
        authenticate();
    }

    protected void authenticate(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.isNull(authentication) || !authentication.isAuthenticated()){
            var token = new UsernamePasswordAuthenticationToken(CRONJOB_USER,null,
                    AuthorityUtils.createAuthorityList(AuthorizationConstants.ADMIN));
            var jwtUserData = new JwtUserData();
            jwtUserData.setJwtId(UUID.randomUUID().toString());
            token.setDetails(jwtUserData);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }

    protected void saveCronJobLog(final CronJobModel cronJobModel, final CronJobLogModel cronJobLogModel,
                                  final CronJobStatus cronJobStatus, final String logStatusDescription){
        cronJobLogModel.setCronJob(cronJobModel);
        cronJobLogModel.setLogStatus(cronJobStatus);
        cronJobLogModel.setLogStatusDescription(logStatusDescription);
        modelService.save(cronJobLogModel);
    }
}
