package btl; // השורה הזו חייבת להיות ראשונה!



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageObject.BtlBasePage;
import pageObject.HomePage;
import pageObject.InsuranceCalculatorPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CalculationTest extends BaseTest {
    @Test
    public void testInsuranceCalculation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // כניסה לדף הבית וניווט
        HomePage homePage = new HomePage(driver);
        homePage.navigate("דמי ביטוח", "דמי ביטוח לאומי");

        //  המתנה לקישור של המחשבון ולחיצה עליו
        WebElement calcLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("מחשבון")));
        calcLink.click();

        //  מילוי פרטים בצעד ראשון
        // בחירת סוג מבוטח (סטודנט/תלמיד ישיבה), מין ותאריך לידה
        wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_rdb_employeType_2"))).click();
        driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_rdb_Gender_0")).click();
        driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_DynDatePicker_BirthDate_Date")).sendKeys("01/01/2000");

        // לחיצה על המשך
        driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_StartNavigationTemplateContainerID_StartNextButton")).click();

        //  בדיקה שהגענו לצעד שני
        // המתנה עד שהטקסט "צעד שני" יופיע בדף
        WebElement step2Header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'צעד שני')]")));
        Assertions.assertTrue(step2Header.isDisplayed(), "הכותרת צעד שני לא הופיעה");

        //  בחירה ב"לא" עבור קצבת נכות
        driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_rdb_GetNechut_1")).click();

        // לחיצה על המשך לסיום
        driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_StepNavigationTemplateContainerID_StepNextButton")).click();

        //  בדיקה שהגענו לשלב סיום
        WebElement finalStep = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'סיום')]")));
        Assertions.assertTrue(finalStep.isDisplayed(), "הכותרת סיום לא הופיעה");

        //  אימות תוצאות החישוב
        // שליפת הטקסט של הסכומים מהדף
        String insuranceAmount = driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_lbl_LeumiInsuranceAmount")).getText();
        String healthAmount = driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_lbl_HealthInsuranceAmount")).getText();
        String totalAmount = driver.findElement(By.id("ctl00_ctl43_g_642b1586_5c41_436a_a04c_e3b5ba94ba69_ctl00_InsuranceNotSachirWizard_lbl_TotalInsurance")).getText();

        // ביצוע Assertions על הסכומים כפי שנדרש
        Assertions.assertTrue(insuranceAmount.contains("48"), "דמי ביטוח לאומי צריכים להיות 48 שח");
        Assertions.assertTrue(healthAmount.contains("123"), "דמי ביטוח בריאות צריכים להיות 123.00 שח");
        Assertions.assertTrue(totalAmount.contains("171"), "סך הכל דמי ביטוח לחודש צריכים להיות 171 שח");

        System.out.println("הטסט עבר בהצלחה: כל הסכומים תואמים לדרישות הפרויקט.");
    }
}