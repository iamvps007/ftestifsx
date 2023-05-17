import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class IncomeFlexDepletionReportJobListener implements JobExecutionListener {

    private final JobParametersValidator jobParametersValidator;
    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;

    private final String errorReportPath = "error_report.txt";

    @Autowired
    public IncomeFlexDepletionReportJobListener(JobParametersValidator jobParametersValidator,
                                               JobLauncher jobLauncher,
                                               JobBuilderFactory jobBuilderFactory) {
        this.jobParametersValidator = jobParametersValidator;
        this.jobLauncher = jobLauncher;
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Perform actions before the job starts
        System.out.println("IncomeFlex Depletion Report job started.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Perform actions after the job completes
        if (jobExecution.getStatus().isUnsuccessful()) {
            System.out.println("IncomeFlex Depletion Report job failed.");
            generateErrorReport(jobExecution);
        } else {
            System.out.println("IncomeFlex Depletion Report job completed successfully.");
        }
    }

    private void generateErrorReport(JobExecution jobExecution) {
        List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
        if (!exceptions.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(errorReportPath))) {
                writer.println("Error Report:");
                writer.println();
                for (Throwable exception : exceptions) {
                    writer.println("Exception:");
                    writer.println(exception.getMessage());
                    writer.println("Stack Trace:");
                    exception.printStackTrace(writer);
                    writer.println();
                }
                writer.flush();
            } catch (IOException e) {
                System.out.println("Failed to generate error report.");
                e.printStackTrace();
            }
        }
    }
}
