package pageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InsuranceCalculatorPage extends BtlBasePage{
    public InsuranceCalculatorPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "BirthDate")
    private WebElement birthDateInput;

    @FindBy(xpath = "//label[contains(text(),'תלמיד ישיבה')]")
    private WebElement yeshivaStudentRadio;

    @FindBy(id = "btnNext")
    private WebElement nextButton;

    @FindBy(xpath = "//label[contains(text(),'לא')]")
    private WebElement noDisabilityRadio;

    @FindBy(id = "lblTotalHealth")
    private WebElement healthInsuranceResult;

    @FindBy(id = "lblTotalSum")
    private WebElement totalSumResult;

    public void calculateYeshivaStudent(String birthDate) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOf(birthDateInput));
        birthDateInput.sendKeys(birthDate);
        yeshivaStudentRadio.click();
        nextButton.click();

        wait.until(ExpectedConditions.visibilityOf(noDisabilityRadio));
        noDisabilityRadio.click();
        nextButton.click();
    }

    public String getTotalSum() {
        return totalSumResult.getText();
    }
}
