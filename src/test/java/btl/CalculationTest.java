package btl; // Must be the first line

import org.junit.jupiter.api.Test;
import pageObject.HomePage;
import pageObject.InsuranceCalculatorPage;

/**
 * Test: insurance-fees calculation for a yeshiva student.
 * Flow: Insurance fees -> National Insurance fees -> calculator -> fill in the wizard -> verify results.
 */
public class CalculationTest extends BaseTest {

    @Test
    public void testInsuranceCalculation() {
        HomePage homePage = new HomePage(driver);
        InsuranceCalculatorPage calculator = new InsuranceCalculatorPage(driver);

        // Navigate: main menu -> sub menu
        homePage.navigate("דמי ביטוח", "דמי ביטוח לאומי");

        // Open the calculator form
        calculator.openCalculator();

        // Fill in the wizard (step 1 and step 2) for a yeshiva student
        calculator.calculateYeshivaStudent("01/01/2000");

        // Wait until the "Finish" step is really displayed
        calculator.waitForFinishStep();

        // Expected results according to the assignment:
        // National Insurance = 48, Health Insurance = 123.00, Total = 171
        calculator.assertResults("48", "123", "171");

        System.out.println("הטסט עבר בהצלחה: כל הסכומים תואמים לדרישות הפרויקט.");
    }
}