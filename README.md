# BTL UI Test Automation

UI test automation for the Israeli National Insurance (BTL) website, built with Java, Selenium and JUnit 5. The project uses the Page Object Model and generates test reports with ExtentReports.

## What Is Tested

**1. Site search**
- Searches for the phrase for "calculating the daily maternity allowance amount" using the search field and the magnifying glass icon
- Verifies that a new page opens with the matching search results title

**2. National Insurance contributions calculator (yeshiva student)**
- Navigates through the main menu to Insurance Contributions, then National Insurance Contributions, and verifies the page title
- Opens the insurance contributions calculator and verifies its page title
- Step 1: selects yeshiva student and enters a date of birth
- Step 2: verifies the step opened, selects "No" for disability pension and continues
- Final step: verifies the final stage opened and checks the calculated results:
  - National Insurance contributions: 48 NIS
  - Health insurance contributions: 123.00 NIS
  - Total monthly contributions: 171 NIS

## Design

- An abstract base test class holds the shared `WebDriver` instance and initializes page elements
- A base page object holds elements and actions shared across pages, such as the main menu (main menu and submenu navigation) and the search field
- Each page of the site has its own page object class, with a method for each step
- Dynamic waits are used while pages load

## Tech Stack

| Area | Technologies |
|------|--------------|
| Language | Java |
| Test framework | JUnit 5 |
| Browser automation | Selenium WebDriver |
| Build tool | Maven |
| Reporting | ExtentReports |
| Design pattern | Page Object Model |

## Project Structure

```
automation-project/
├── src/         # Page objects and tests
└── pom.xml      # Maven dependencies and configuration
```

## Getting Started

### Prerequisites

- JDK [add the version you use]
- Maven
- [Add the browser you run the tests on, e.g. Chrome]

### Run the tests

```bash
mvn test
```

### Reports

After a run, the ExtentReports HTML report is created at `[add the real report path]`.
