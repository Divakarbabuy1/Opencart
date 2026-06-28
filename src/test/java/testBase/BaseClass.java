package testBase;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Foundational Base Class for the Test Automation Framework.
 * Configures global test lifecycle actions, dynamic driver initialization,
 * logging infrastructures, and shared utility helpers.
 */
public class BaseClass {

    // ==========================================
    // GLOBAL SHARED RESOURCES
    // ==========================================

    // ThreadLocal container ensuring safe isolated drivers for concurrent execution tracks
    public static ThreadLocal<WebDriver> tdriver = new ThreadLocal<>();
    public Logger logger;
    public Properties prop;

    public static WebDriver getDriver() {
        return tdriver.get();
    }

    // ==========================================
    // DRIVER CONFIGURATION & LIFECYCLE
    // ==========================================

    @BeforeMethod(alwaysRun = true, groups = {"Smoke", "Regression", "Sanity", "DataDriven"})
    @Parameters({"os", "browser"})
    public void setup(@Optional("mac") String os, @Optional("chrome") String br) throws IOException {

        FileReader file = new FileReader("./src/test/resources/config.properties");
        prop = new Properties();
        prop.load(file);
        logger = LogManager.getLogger(this.getClass());

        // REMOTE GRID EXECUTION LOGIC
        if (prop.getProperty("env_execute").equalsIgnoreCase("remote")) {
            switch (br.toLowerCase()) {
                case "chrome":
                    ChromeOptions coptions = new ChromeOptions();
                    coptions.setPlatformName("LINUX");
                    tdriver.set(new RemoteWebDriver(new URL("http://localhost:4444"), coptions));
                    break;
                case "edge":
                    EdgeOptions eoptions = new EdgeOptions();
                    eoptions.setPlatformName("LINUX");
                    tdriver.set(new RemoteWebDriver(new URL("http://localhost:4444"), eoptions));
                    break;
                case "firefox":
                    FirefoxOptions foptions = new FirefoxOptions();
                    foptions.setPlatformName("LINUX");
                    tdriver.set(new RemoteWebDriver(new URL("http://localhost:4444"), foptions));
                    break;
                default:
                    logger.error("Invalid browser name provided : " + br);
                    throw new RuntimeException("Execution stopped : Browser not supported");
            }
        }
        // LOCAL STANDALONE EXECUTION LOGIC
        else if (prop.getProperty("env_execute").equalsIgnoreCase("local")) {
            switch (br.toLowerCase()) {
                case "chrome":
                    tdriver.set(new ChromeDriver());
                    break;
                case "edge":
                    tdriver.set(new EdgeDriver());
                    break;
                case "firefox":
                    tdriver.set(new FirefoxDriver());
                    break;
                default:
                    logger.error("Invalid browser name provided : " + br);
                    throw new RuntimeException("Execution stopped : Browser not supported");
            }
        }
        getDriver().manage().deleteAllCookies();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        getDriver().get(prop.getProperty("appurl"));
    }

    @AfterMethod(alwaysRun = true, groups = {"Smoke", "Regression", "Sanity", "DataDriven"})
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            tdriver.remove();
        }
    }

    // ==========================================
    // DYNAMIC TEST DATA GENERATION UTILS
    // ==========================================

    public String randomString() {
        String generatedString = RandomStringUtils.randomAlphabetic(5);
        return generatedString;
    }

    public String randomEmail() {
        String generatedString = RandomStringUtils.randomAlphabetic(5);
        return generatedString + "@gmail.com";
    }

    public String randomPhoneNumber() {
        String generatedNumber = RandomStringUtils.randomNumeric(10);
        return generatedNumber;
    }

    public String randomAlphaNumeric() {
        String str = RandomStringUtils.randomAlphabetic(3);
        String num = RandomStringUtils.randomNumeric(3);
        return (str + "@" + num);
    }

    // ==========================================
    // TEST EVIDENCE REPORTING UTILS
    // ==========================================

    /**
     * Extracts and snapshots current viewable driver display as evidence artifact.
     * @param tname Meaningful running test method name.
     * @return String representing absolute file pathway location of snapshot.
     */
    public String captureScreen(String tname) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddhhss").format(new Date());
        TakesScreenshot takesScreenshot = (TakesScreenshot) getDriver();
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        String targetFilePath = System.getProperty("user.dir") + "//screenshots//" + tname + "_" + timeStamp + ".png";
        File targetFile = new File(targetFilePath);
        sourceFile.renameTo(targetFile);
        return targetFilePath;
    }
}