package com.capella.cronjob.impl;

import com.capella.cronjob.CronJob;
import com.capella.domain.enums.CronJobStatus;
import com.capella.domain.model.cronjoblog.CronJobLogModel;
import com.capella.domain.model.currency.CurrencyModel;
import com.capella.domain.model.exchangerate.ExchangeRateModel;
import com.capella.service.exception.model.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component(value = ExchangeRateCronJob.CRONJOB_NAME)
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateCronJob extends CronJob {
    public final static String CRONJOB_NAME = "exchange_rate_recorder";
    public final static String TURKISH_LIRA_CODE = "TRY";
    public final static String TURKISH_LIRA = "Türk Lirası";

    @Value("${tcmb.exchange.rate.xml}")
    private String exchangeRateXml;

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

                exchangeRateRecorder();

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

    @SneakyThrows
    private void exchangeRateRecorder(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new URL(exchangeRateXml).openStream());
        var date = new Date();
        var currentDate = Date.from(
                date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .truncatedTo(ChronoUnit.DAYS)
                        .toInstant()
        );
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("Currency");
        for(int i=0; i<nodeList.getLength(); i++){
            Node node = nodeList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){
                var element = (Element) node;
                var currencyCode = element.getAttribute("Kod");
                var currencyName = element.getElementsByTagName("CurrencyName").item(0).getTextContent();
                var bankNoteBuying = element.getElementsByTagName("BanknoteBuying").item(0).getTextContent();
                var bankNoteSelling = element.getElementsByTagName("BanknoteSelling").item(0).getTextContent();
                var crossRateUsd = element.getElementsByTagName("CrossRateUSD").item(0).getTextContent();


                CurrencyModel currencyModel;
                try{
                    currencyModel = currencyService.getCurrencyModel(TURKISH_LIRA_CODE);
                }catch (ModelNotFoundException e){
                    currencyModel = modelService.create(CurrencyModel.class);
                    currencyModel.setCode(TURKISH_LIRA_CODE);
                    currencyModel.setShortText(TURKISH_LIRA);
                    currencyModel.setLongText(TURKISH_LIRA);
                    currencyModel.setSearchText(TURKISH_LIRA);
                }

                ExchangeRateModel exchangeRateModel;

            }
        }
    }
}
