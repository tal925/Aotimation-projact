package pageObject;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page object for the National Insurance (BTL) insurance-fees calculator.
 * Covers the whole wizard: opening the calculator, filling in the steps for a
 * yeshiva student, and reading/verifying the final results.
 */
public class InsuranceCalculatorPage extends BtlBasePage {

    public InsuranceCalculatorPage(WebDriver driver) {
        super(driver);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Safe click: scrolls the element to the center of the viewport (not to the very top,
     * where the sticky header would cover it) and clicks it. If the regular click fails
     * because the element is hidden or has zero size (custom-styled radio buttons),
     * it falls back to a JavaScript click.
     */
    private void safeClick(WebDriverWait wait, WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        try {
            element.click();
        } catch (ElementNotInteractableException e) {
            // Note: ElementClickInterceptedException is a subclass of this exception,
            // so this single catch handles both cases.
            System.out.println("safeClick(): קליק רגיל נכשל (" + e.getClass().getSimpleName()
                    + "), מבצע קליק דרך JavaScript");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // ------------------------------------------------------------------
    // Page elements
    // ------------------------------------------------------------------

    // Gender: "male" radio button
    @FindBy(xpath = "//label[contains(text(),'זכר')] | //input[contains(@id,'rdb_Sex_1')] | //span[contains(text(),'זכר')]")
    private WebElement maleGenderRadio;

    // The real id/name contain a GUID that can change between deployments, so we match
    // on the stable suffix "DynDatePicker_BirthDate_Date" instead of the full id.
    @FindBy(xpath = "//input[contains(@id,'DynDatePicker_BirthDate_Date')]")
    private WebElement birthDateInput;

    // "Yeshiva student" option in the employment-type radio group
    @FindBy(xpath = "//label[contains(text(),'תלמיד ישיבה')] | //input[contains(@id,'rdb_employeType_2')] | //span[contains(text(),'תלמיד ישיבה')]")
    private WebElement yeshivaStudentRadio;

    // "Continue" button (the same button is reused in every step of the wizard)
    @FindBy(xpath = "//input[contains(@id,'StartNextButton')] | //input[@value='המשך'] | //button[contains(text(),'המשך')] | //a[contains(text(),'המשך')]")
    private WebElement nextButton;

    // "No" answer for the disability-allowance question. It is located relative to the
    // question text: the second radio input after it ("Yes" is first, "No" is second).
    // This avoids matching a "No" label from somewhere else on the page (footer, other questions).
    @FindBy(xpath = "(//*[contains(text(),'קצבת נכות')]/following::input[@type='radio'])[2]")
    private WebElement noDisabilityRadio;

    // ------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------

    /**
     * Opens the insurance calculator form. Expects the "National Insurance" page to be open.
     * Two clicks are needed: the calculator link, and then the category button on the
     * intermediate page.
     */
    public void openCalculator() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            System.out.println("openCalculator(): מחפש קישור 'מחשבון'...");
            WebElement calcLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("מחשבון")));
            System.out.println("openCalculator(): נמצא, לוחץ -> href='" + calcLink.getAttribute("href") + "'");
            calcLink.click();
            System.out.println("openCalculator(): הקליק בוצע, כתובת נוכחית: " + driver.getCurrentUrl());

            // We land on an intermediate page that lists categories (self-employed / student /
            // abroad / not working). One more click is needed to reach the actual form
            // that contains the birth-date field.
            System.out.println("openCalculator(): מחפש כפתור קטגוריית 'עצמאי, תלמיד...'...");
            By categoryButtonLocator = By.xpath(
                    "//a[contains(.,'עצמאי') and contains(.,'תלמיד')] | //button[contains(.,'עצמאי') and contains(.,'תלמיד')]"
            );
            WebElement categoryButton = wait.until(ExpectedConditions.elementToBeClickable(categoryButtonLocator));
            System.out.println("openCalculator(): נמצא כפתור קטגוריה, לוחץ -> text='" + categoryButton.getText() + "'");
            categoryButton.click();
            System.out.println("openCalculator(): אחרי קליק קטגוריה, כתובת נוכחית: " + driver.getCurrentUrl());

            // Make sure the form is really loaded (birth-date field visible) before returning
            wait.until(ExpectedConditions.visibilityOf(birthDateInput));
            System.out.println("openCalculator(): טופס החישוב נטען בהצלחה (שדה תאריך לידה גלוי)");
        } catch (Exception e) {
            System.out.println("openCalculator(): שגיאה: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Fills in the calculator wizard for a yeshiva student.
     * Step 1: gender, birth date, employment type. Step 2: disability allowance = "No".
     *
     * @param birthDate birth date in dd/MM/yyyy format, e.g. "01/01/2000"
     */
    public void calculateYeshivaStudent(String birthDate) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            // Step 1 - gender is a required field, the wizard will not continue without it
            System.out.println("calculateYeshivaStudent(): לוחץ על מין 'זכר'...");
            safeClick(wait, maleGenderRadio);

            System.out.println("calculateYeshivaStudent(): ממתין לשדה תאריך לידה...");
            wait.until(ExpectedConditions.visibilityOf(birthDateInput));
            birthDateInput.clear();
            birthDateInput.sendKeys(birthDate);
            // Leave the field to close a possible date-picker popup that could block the next clicks
            birthDateInput.sendKeys(Keys.TAB);
            System.out.println("calculateYeshivaStudent(): תאריך לידה הוזן: " + birthDate);

            System.out.println("calculateYeshivaStudent(): לוחץ על 'תלמיד ישיבה'...");
            safeClick(wait, yeshivaStudentRadio);

            System.out.println("calculateYeshivaStudent(): לוחץ 'המשך' (שלב 1)...");
            safeClick(wait, nextButton);
            System.out.println("calculateYeshivaStudent(): אחרי 'המשך' 1, כתובת: " + driver.getCurrentUrl());

            // Step 2 - disability allowance: "No".
            // The wizard is AJAX based, so the URL stays the same between steps.
            System.out.println("calculateYeshivaStudent(): לוחץ 'לא' (נכות)...");
            safeClick(wait, noDisabilityRadio);
            System.out.println("calculateYeshivaStudent(): [אבחון] 'לא' (נכות) מסומן בפועל? " + noDisabilityRadio.isSelected());

            System.out.println("calculateYeshivaStudent(): לוחץ 'המשך' (שלב 2)...");
            safeClick(wait, nextButton);
            System.out.println("calculateYeshivaStudent(): אחרי 'המשך' 2, כתובת: " + driver.getCurrentUrl());

            // Non-blocking diagnostic check: did we actually reach the "Finish" step?
            // (Only logs - the real verification is done in the test via waitForFinishStep().)
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                shortWait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.xpath("//*[contains(text(),'סיום')]")));
                System.out.println("calculateYeshivaStudent(): [אבחון] 'סיום' נמצא בעמוד \u2713");
            } catch (Exception diag) {
                System.out.println("calculateYeshivaStudent(): [אבחון] 'סיום' לא נמצא אחרי 5 שניות - " +
                        "ייתכן שהטופס לא התקדם. כתובת נוכחית: " + driver.getCurrentUrl());
                System.out.println("calculateYeshivaStudent(): [אבחון] כותרת עמוד נוכחית: " + driver.getTitle());
            }

        } catch (Exception e) {
            System.out.println("calculateYeshivaStudent(): שגיאה: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ------------------------------------------------------------------
    // Results
    // ------------------------------------------------------------------

    /**
     * Waits until the "Finish" heading is actually displayed. Uses an exact text match and
     * checks visibility, because hidden wizard steps may contain the same text.
     */
    public void waitForFinishStep() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        By finishHeading = By.xpath("//*[normalize-space(text())='סיום']");
        wait.until(d -> d.findElements(finishHeading).stream().anyMatch(WebElement::isDisplayed));
        System.out.println("waitForFinishStep(): הגענו לשלב סיום");
    }

    /**
     * Returns the text of the results box (the one that starts with "Based on the data entered...").
     * Starting from that heading we walk up through the enclosing divs until we find the one that
     * contains all result lines. This way we do not depend on auto-generated ids, and we do not
     * accidentally read the "National Insurance" text from the top menu.
     */
    private String getResultsText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        By heading = By.xpath("//*[contains(text(),'על פי הנתונים שהוזנו')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(heading));
        for (int level = 1; level <= 5; level++) {
            List<WebElement> containers = driver.findElements(
                    By.xpath("//*[contains(text(),'על פי הנתונים שהוזנו')]/ancestor::div[" + level + "]"));
            if (containers.isEmpty()) {
                break;
            }
            String text = containers.get(0).getText();
            if (text.contains("דמי ביטוח לאומי") && text.contains("דמי ביטוח בריאות")) {
                return text;
            }
        }
        throw new NoSuchElementException("לא נמצאה תיבת התוצאות עם שורות הסכומים");
    }

    /**
     * Extracts the number that follows a label in the results text.
     * Example: label "דמי ביטוח בריאות" in "...דמי ביטוח בריאות: 123.00 ש"ח" returns "123.00".
     */
    private String extractAmount(String text, String label) {
        Matcher m = Pattern.compile(Pattern.quote(label) + "\\s*:?\\s*([0-9]+(?:\\.[0-9]+)?)").matcher(text);
        if (!m.find()) {
            throw new NoSuchElementException("לא נמצא סכום עבור '" + label + "' בטקסט: " + text);
        }
        return m.group(1);
    }

    /** @return the "National Insurance" fee amount from the results box, e.g. "48" */
    public String getLeumiInsuranceAmount() {
        return extractAmount(getResultsText(), "דמי ביטוח לאומי");
    }

    /** @return the "Health Insurance" fee amount from the results box, e.g. "123.00" */
    public String getHealthInsuranceAmount() {
        return extractAmount(getResultsText(), "דמי ביטוח בריאות");
    }

    /** @return the total monthly insurance fee from the results box, e.g. "171" */
    public String getTotalSum() {
        return extractAmount(getResultsText(), "סך הכל דמי ביטוח לחודש");
    }

    /** Compares two amounts numerically, so "123.00" equals "123" (unlike a plain string comparison). */
    private void assertAmount(String description, String actual, String expected) {
        Assertions.assertEquals(0, new BigDecimal(actual).compareTo(new BigDecimal(expected)),
                description + " לא תואם - צפוי: " + expected + ", נמצא: " + actual);
    }

    /** Verifies the National Insurance and Health Insurance amounts. */
    public void assertResults(String expectedLeumi, String expectedHealth) {
        try {
            String leumi = getLeumiInsuranceAmount();
            String health = getHealthInsuranceAmount();
            System.out.println("assertResults(): leumi='" + leumi + "', health='" + health + "'");
            assertAmount("דמי ביטוח לאומי", leumi, expectedLeumi);
            assertAmount("דמי ביטוח בריאות", health, expectedHealth);
        } catch (Exception e) {
            System.out.println("assertResults(): שגיאה: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /** Verifies the National Insurance, Health Insurance and total monthly amounts. */
    public void assertResults(String expectedLeumi, String expectedHealth, String expectedTotal) {
        assertResults(expectedLeumi, expectedHealth);
        String total = getTotalSum();
        System.out.println("assertResults(): total='" + total + "'");
        assertAmount("סך הכל דמי ביטוח לחודש", total, expectedTotal);
    }
}