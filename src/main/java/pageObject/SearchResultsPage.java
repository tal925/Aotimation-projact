package pageObject;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SearchResultsPage extends BtlBasePage {
    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public void verifySearchResultTitle(String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement resultTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='results']//h2 | //h2[contains(text(),'תוצאות חיפוש')]"))
        );

        Assertions.assertTrue(resultTitle.getText().contains(expectedText),
                "כותרת דף התוצאות אינה מכילה את הטקסט הצפוי.\nActual: " + resultTitle.getText());
    }
}
