package pageObject;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

/**
 * Base page object for the BTL (National Insurance) site.
 * Holds the elements and actions shared by several pages: the main menu and the site search.
 */
public class BtlBasePage extends BasePage {

    // Search button (magnifying glass icon)
    @FindBy(xpath = "//*[@id='ctl00_SiteHeader_reserve_btnSearch']")
    private WebElement serchButton;

    // Search input field
    @FindBy(id = "TopQuestions")
    WebElement searchInput;

    // Main menu container. Used as a search scope for the menu links, so we do not
    // accidentally match an identical link in the footer, a hidden mobile menu, or elsewhere on the page.
    @FindBy(xpath = "//div[@class=\"megamenu_wrapper megamenu_light_theme\"]")
    private WebElement menu;

    // Search results page title
    @FindBy(xpath = "//*[@id='results']//h2")
    WebElement resultTitle;

    public BtlBasePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Clicks an item in the main menu and then an item in its sub menu.
     *
     * @param mainMenuText text of the main menu item, e.g. "דמי ביטוח"
     * @param subMenuText  text of the sub menu item, e.g. "דמי ביטוח לאומי"
     */
    public void navigate(String mainMenuText, String subMenuText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Remember the open windows before clicking, so we can detect a newly opened tab
        Set<String> handlesBeforeClick = driver.getWindowHandles();
        String originalWindow = driver.getWindowHandle();

        try {
            // Search only inside the main menu element (note the leading "." in the XPath),
            // not the whole page - avoids clashing with identical links in the footer / hidden mobile menu
            wait.until(ExpectedConditions.visibilityOf(menu));
            By mainMenuLocator = By.xpath(".//a[contains(normalize-space(.), '" + mainMenuText + "')]");
            WebElement mainMenuLink = wait.until(d -> {
                WebElement el = menu.findElement(mainMenuLocator);
                return el.isDisplayed() ? el : null;
            });
            System.out.println("navigate(): לוחץ על תפריט ראשי -> text='" + mainMenuLink.getText()
                    + "' href='" + mainMenuLink.getAttribute("href") + "'");
            mainMenuLink.click();

            // The sub menu item may be rendered outside the "menu" element (depends on the DOM),
            // so this search is global, but still limited by a bounded wait.
            By subMenuLocator = By.xpath("//a[contains(normalize-space(.), '" + subMenuText + "')]");
            WebElement subMenuLink = wait.until(ExpectedConditions.elementToBeClickable(subMenuLocator));
            System.out.println("navigate(): לוחץ על תת-תפריט -> text='" + subMenuLink.getText()
                    + "' href='" + subMenuLink.getAttribute("href") + "'");
            subMenuLink.click();

            // If the clicks opened a new tab/window, switch to it explicitly,
            // so the driver does not stay "stuck" on an old or closed window
            Set<String> handlesAfterClick = driver.getWindowHandles();
            if (handlesAfterClick.size() > handlesBeforeClick.size()) {
                for (String handle : handlesAfterClick) {
                    if (!handlesBeforeClick.contains(handle)) {
                        driver.switchTo().window(handle);
                        System.out.println("navigate(): זוהה חלון/טאב חדש, עברתי אליו: " + handle);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("navigate(): שגיאה בזמן ניווט בתפריט (" + mainMenuText + " -> " + subMenuText + "): "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Types a value into the site search field, clicks the search icon,
     * and verifies that the results title contains the searched text.
     *
     * @param searchValue the text to search for
     */
    public void searchSite(String searchValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            wait.until(ExpectedConditions.visibilityOf(searchInput)).clear();
            searchInput.sendKeys(searchValue);
            wait.until(ExpectedConditions.elementToBeClickable(serchButton)).click();
            // Wait for the results page title and check that it mentions the searched text
            WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='results']//h2 | //h2[contains(text(),'תוצאות חיפוש')]"))
            );
            Assertions.assertTrue(result.getText().contains(searchValue),
                    "התוצאה לא כוללת את החיפוש: " + searchValue + "\nText was: " + result.getText());
        } catch (Exception e) {
            System.out.println("searchSite(): שגיאה בזמן חיפוש עבור '" + searchValue + "': "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}