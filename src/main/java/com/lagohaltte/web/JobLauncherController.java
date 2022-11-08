package com.lagohaltte.web;

import com.lagohaltte.config.JobConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class JobLauncherController {
    private final JobConfiguration jobConfiguration;
    private final JobLauncher jobLauncher;

    @GetMapping("/excute/batchjob")
    public void startStockCrawling() throws Exception {
        log.info("startstockcrawling");
        jobLauncher.run(jobConfiguration.listedCorporationsJob(),new JobParameters());
    }
}
