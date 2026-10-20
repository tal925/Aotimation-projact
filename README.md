# BTL UI Test Automation

UI test automation for the Israeli National Insurance (BTL) website, built with Java, Selenium and JUnit 5. The project uses the Page Object Model and generates an HTML report with ExtentReports, including a screenshot for each test.

## What Is Tested

| Test class | Test | What it checks |
|------------|------|----------------|
| `SearchTest` | `testSearchAndTitle` | Searches for the phrase for "calculating the daily maternity allowance amount" and checks the search results page title and heading |
| `NavigationTest` | `testNavigationToInsurancePage` | Navigates through the main menu to Insurance Contributions and checks that the insurance page opened |
| `CalculationTest` | `testInsuranceCalculation` | Runs the insurance contributions calculator flow for a yeshiva student, checks that the final step is displayed, and checks the National Insurance (48) and health insurance (123) results |

## Design

| Class | Responsibility |
|-------|----------------|
| `BaseTest` | Abstract test base: Chrome driver setup and teardown, shared test lifecycle, ExtentReports and screenshots |
| `BasePage` | Abstract page base: stores the driver and initializes page elements with Selenium `PageFactory` |
| `BtlBasePage` | Shared site elements and actions: main menu navigation and the search field |
| `HomePage` | Home page; its search method returns a `SearchResultsPage` |
| `SearchResultsPage` | Waits for and verifies the search results heading |
| `InsuranceCalculatorPage` | Calculator interactions, result retrieval and result assertions |

The tests use explicit waits (`WebDriverWait` with `ExpectedConditions`) instead of fixed sleeps.

## Tech Stack

| Area | Technologies |
|------|--------------|
| Language | Java 21 |
| Test framework | JUnit 5 (5.14.0) |
| Browser automation | Selenium WebDriver (4.38.0), WebDriverManager |
| Build tool | Maven |
| Reporting | ExtentReports (5.1.2) |
| Design pattern | Page Object Model |

## Getting Started

### Prerequisites

- JDK 21 or newer
- Maven
- Google Chrome (the driver is downloaded automatically by WebDriverManager)

### Run the tests

From the project root:

```bash
mvn test
```

To clean previous build output first:

```bash
mvn clean test
```

Or run the tests from an IDE (IntelliJ IDEA or Eclipse): open the project as a Maven project, then run a test class as a JUnit test.

### Reports

After a run, the ExtentReports HTML report is created at `target/reports/extent-report.html`. A screenshot is taken after every test, saved in `target/screenshots` and attached to the report.

## Known Limitations

- The navigation test accepts any one of several signals (URL, page title or page content) as proof that the page opened
- The calculator test checks the National Insurance and health insurance amounts, but not the monthly total
- The project does not include parameterized tests yet
