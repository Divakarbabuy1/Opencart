package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testBase.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom Extent Report TestNG Listener.
 * Orchestrates test execution monitoring, dynamic charting, failed screenshot attachments,
 * and automated email transmissions post-suite termination.
 */
public class ExtentReportManager implements ITestListener {

    // ==========================================
    // GLOBAL REPORTING HANDLES
    // ==========================================
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;
    public static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    public Properties prop;
    private String repName;

    // ==========================================
    // LIFECYCLE HOOKS: SUITE EXECUTION START
    // ==========================================
    @Override
    public void onStart(ITestContext testContext) {

        try {
            FileReader file = new FileReader("./src/test/resources/config.properties");
            prop = new Properties();
            prop.load(file);
        } catch (Exception e) {
            logger.error("Failed to load config.properties in Report Manager: " + e.getMessage());
        }

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Test-Report-" + timeStamp + ".html";

        String reportDirectory = System.getProperty("user.dir") + File.separator + "reports" + File.separator + repName;
        sparkReporter = new ExtentSparkReporter(reportDirectory);

        sparkReporter.config().setDocumentTitle("OpenCart Automation Report");
        sparkReporter.config().setReportName("OpenCart Functional Testing");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "OpenCart");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("Sub Module", "Customers");

        String tester = prop.getProperty("TesterName", "Default Tester");
        extent.setSystemInfo("User Name", tester);
        extent.setSystemInfo("Environment", "QA");

        String os = testContext.getCurrentXmlTest().getParameter("os");
        extent.setSystemInfo("Operating System", os);

        String browser = testContext.getCurrentXmlTest().getParameter("browser");
        extent.setSystemInfo("Browser", browser);

        List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
        if (!includedGroups.isEmpty()) {
            extent.setSystemInfo("Groups", includedGroups.toString());
        }
    }

    // ==========================================
    // LIFECYCLE HOOKS: ATOMIC TEST TRACKING
    // ==========================================
    @Override
    public void onTestSuccess(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.PASS, result.getName() + " got successfully executed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // FIXED: Swapped result.getClass() with result.getTestClass()
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.FAIL, result.getName() + " got failed");
        test.log(Status.INFO, result.getThrowable().getMessage());

        try {
            String imgPath = new BaseClass().captureScreen(result.getName());
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e) {
            logger.error("Failed to bind screen snapshot artifact: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // FIXED: Swapped result.getClass() with result.getTestClass()
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.SKIP, result.getName() + " got skipped");
        test.log(Status.INFO, result.getThrowable().getMessage());
    }

    // ==========================================
    // LIFECYCLE HOOKS: SUITE EXECUTION TERMINATION
    // ==========================================
    @Override
    public void onFinish(ITestContext testContext) {
        extent.flush();

        String pathOfExtentReport = System.getProperty("user.dir") + File.separator + "reports" + File.separator + repName;
        File extentReport = new File(pathOfExtentReport);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                logger.info("Local UI run detected. Automatically opening the Extent Report in browser.");
                Desktop.getDesktop().browse(extentReport.toURI());
            } else {
                logger.info("Terminal/Headless run detected. Skipping browser auto-open. Report generated at: " + pathOfExtentReport);
            }
        } catch (Exception e) {
            logger.warn("Could not auto-open report browser window: " + e.getMessage());
        }

        try {
            String sendReportFlag = prop.getProperty("email.sendReport", "false");
            if ("true".equalsIgnoreCase(sendReportFlag)) {
                String recipients = prop.getProperty("email.recipients", "");
                if (!recipients.isEmpty()) {
                    logger.info("Initiating email dispatch sequence to: " + recipients);

                    EmailUtility.sendEmailWithReport(pathOfExtentReport, recipients);

                    logger.info("Email handoff completed successfully.");
                    logger.info("Applying 3-second network buffer for terminal execution safety...");
                    Thread.sleep(3000);
                } else {
                    logger.warn("Email recipients not configured in config.properties");
                }
            } else {
                logger.info("Email sending is disabled. Set email.sendReport=true to enable.");
            }
        } catch (Exception e) {
            logger.error("Failed to send execution email: " + e.getMessage());
        }
    }
}