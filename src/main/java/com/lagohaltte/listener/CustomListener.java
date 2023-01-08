package com.lagohaltte.listener;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomListener implements StepExecutionListener {
    @Override
    public void beforeStep(@NotNull StepExecution stepExecution) {
        log.info("--beforeStep");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("--afterStep");
        String stepName = stepExecution.getStepName();
        log.error("  stepName :{}", stepName);
        return stepExecution.getExitStatus();
    }
}
