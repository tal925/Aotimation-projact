package btl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageObject.HomePage;


public class SearchTest extends BaseTest{
    @Test
    public void testSearchAndTitle() {
        HomePage homePage = new HomePage(driver);
        homePage.searchSite("חישוב סכום דמי לידה ליום");
        String expectedTitle = "תוצאות חיפוש עבור חישוב סכום דמי לידה ליום";
        Assertions.assertEquals(driver.getTitle(), expectedTitle);
    }
}
