package btl; // Must be the first line

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for all tests. Responsible for:
 * - creating the Extent HTML report once per run,
 * - opening a fresh Chrome browser on the BTL home page before every test,
 * - taking a screenshot (attached to the report) and closing the browser after every test.
 */
public abstract class BaseTest {

    protected static ExtentReports extent;
    protected static ExtentTest test;
    protected WebDriver driver;

    /** Runs once before all tests: creates the report folder and the Extent report. */
    @BeforeAll
    public static void beforeAllTest() {
        System.out.println("פתיחת דוח עבור ההרצה");
        Path reportFolder = Paths.get("target", "reports");
        try {
            Files.createDirectories(reportFolder);
        } catch (IOException e) {
            throw new RuntimeException("לא ניתן ליצור תיקייה לדוח", e);
        }
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFolder.resolve("extent-report.html").toFile());
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    /** Runs before every test: starts Chrome, opens the BTL home page and creates a report entry. */
    @BeforeEach
    public void beforeEachTest(TestInfo testInfo) {
        // Lets WebDriverManager download a chromedriver that matches the installed Chrome version
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        // Stability flags for local runs (mainly on Windows) - help avoid browser crashes
        // that end in "invalid session id"
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://www.btl.gov.il/Pages/default.aspx");

        // Create a report entry named after the test method
        String testName = testInfo.getTestMethod().map(method -> method.getName()).orElse("Unnamed test");
        test = extent.createTest(testName);
    }

    /** Runs after every test: attaches a screenshot to the report and always closes the browser. */
    @AfterEach
    public void afterEachTest() {
        if (driver != null) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path screenshotFolder = Paths.get("target", "screenshots");
                Files.createDirectories(screenshotFolder);
                // A timestamp in the file name keeps every screenshot unique
                String fileName = "screenshot_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")) + ".png";
                Path targetPath = screenshotFolder.resolve(fileName);
                Files.copy(screenshot.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                if (test != null) {
                    test.addScreenCaptureFromPath(targetPath.toString());
                }
            } catch (Exception e) {
                // Print the full stack trace to see exactly where and why the session failed
                System.out.println("לא הצליח לצלם מסך: " + e.getMessage());
                e.printStackTrace();
                if (test != null) {
                    test.warning("לא הצליח לצלם מסך: " + e.getMessage());
                }
            }
        }
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // The session was already invalid - nothing to do except log it
                System.out.println("שגיאה בסגירת הדפדפן (ייתכן שכבר נסגר): " + e.getMessage());
            }
        }
    }

    /** Runs once after all tests: writes the Extent report to disk. */
    @AfterAll
    public static void afterAllTest() {
        System.out.println("אחרי כל הטסטים");
        if (extent != null) {
            extent.flush();
        }
    }
}