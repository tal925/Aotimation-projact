package btl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pageObject.HomePage;

public class NavigationTest extends BaseTest {
    @Test
    public void testNavigationToInsurancePage() {
        HomePage homePage = new HomePage(driver);
        homePage.navigate("דמי ביטוח", "דמי ביטוח לאומי");

        Assertions.assertTrue(driver.getCurrentUrl().contains("btl.gov.il") ||
                        driver.getTitle().contains("דמי ביטוח") ||
                        driver.getPageSource().contains("דמי ביטוח לאומי"),
                "הניווט לדף דמי ביטוח לאומי לא בוצע כראוי");
    }
}
