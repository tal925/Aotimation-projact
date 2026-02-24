package btl; // חובה שזו תהיה השורה הראשונה

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public abstract class BaseTest {

protected static ExtentReports extent;
    protected static ExtentTest test;

    protected WebDriver driver;

    @AfterEach
    public void afterEachTest() throws InterruptedException {
        Thread.sleep(3000);
        driver.quit();
    }

    @BeforeAll
    public static void beforeAllTest() {
        System.out.println("פתיחת דוח עבור ההרצה");

    }




    @AfterAll
    public static void afterAllTest() {
        System.out.println("אחרי כל הטסטים");
        //סגירת הדוח, הדוח יישמר בנתיב שהגדרנו כשאתחלנו את הדוח בתחילת ההרצה
        extent.flush();

    }

    @BeforeEach
    public void beforeEachTest() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.btl.gov.il/Pages/default.aspx");
    }

}