package pageObject;

import org.openqa.selenium.WebDriver;

public class HomePage extends BtlBasePage {
    public HomePage(WebDriver driver) {
        super(driver);
    }

    public SearchResultsPage searchFor(String searchValue) {
        searchSite(searchValue);
        return new SearchResultsPage(driver);
    }
}
