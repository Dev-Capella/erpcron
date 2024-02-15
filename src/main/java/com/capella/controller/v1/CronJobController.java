package com.capella.controller.v1;

import com.capella.domain.data.restservice.ServiceResponseData;
import com.capella.domain.enums.ProcessStatus;
import com.capella.facade.CronJobFacade;
import com.capella.util.CronJobConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("cronJobControllerV1")
@RequestMapping(CronJobConstants.VERSION_V1 + CronJobConstants.CRONJOB)
@RequiredArgsConstructor
@Slf4j
public class CronJobController {
    protected final CronJobFacade cronJobFacade;

    @GetMapping(CronJobConstants.RUN + CronJobConstants.CODE)
    public ServiceResponseData run(@PathVariable String code){
        log.info("Inside run of CronJobController",code);
        cronJobFacade.run(code);
        var response = new ServiceResponseData();
        response.setStatus(ProcessStatus.SUCCESS);
        return response;
    }
}
