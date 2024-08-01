package base;

import com.google.auto.common.Visibility;
import groovy.util.logging.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.Assert;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;



public class BaseTests {
    protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(BaseTests.class);
    WebDriver driver;
    WebElement bankManagerLoginBtn;
    WebElement addCustomerBtn;
    WebElement firstNameInput;
    WebElement lastNameInput;
    WebElement postCodeInput;
    WebElement submitCustomerBtn;
    WebElement viewExistedCustomersBtn;
    WebElement openAccountBtn;
    WebElement customerDropDown;
    WebElement currencyDropDown;
    WebElement selectLastCreatedCustomer;
    WebElement selectCurrency;
    WebElement processOpenAccBtn;
    WebElement homeBtn;
    WebElement customerLogin;
    WebElement specificCustomerLoginBtn;
    WebElement accountNumberValue;
    String alertMessage;
    FluentWait wait;
    String customerId;
    String accountNumber;
    Select select;
    int listSize;
    String fName = "Test" + RandomStringUtils.randomNumeric(4);
    String lName = "Last" + RandomStringUtils.randomNumeric(4);
    String PostCode = RandomStringUtils.randomNumeric(5);

    public static void main(String[] args) throws InterruptedException {
        BaseTests test = new BaseTests();
        test.setUp();
        test.scenario1("Lol", "Test", "12345");
        test.teardown();
    }

    @BeforeMethod
    public void setUp() throws InterruptedException {
        String log4jConfPath = "src/test/java/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        //For Edge Driver: msedgedriver.exe
        System.setProperty("webdriver.edge.driver", "resources\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        driver = new EdgeDriver(options);
        driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
        //Declare and initialise a fluent wait
        wait = new FluentWait(driver);
        waitForPageLoad(driver, 3);
    }

    @Test(dataProvider = "CustomerTestDataFeed")
    public void scenario1(String firstName, String lastName, String postCode) {
        //wait.until(ExpectedConditions.visibilityOf(bankManagerLoginBtn));
        bankManagerLoginBtn = driver.findElement(By.xpath("//button[@ng-click='manager()']"));
        wait.until(ExpectedConditions.visibilityOf(bankManagerLoginBtn));
        bankManagerLoginBtn.click();
        addCustomerBtn = driver.findElement(By.xpath("//button[@ng-click='addCust()']"));
        addCustomerBtn.click();
        firstNameInput = driver.findElement(By.xpath("//input[@ng-model='fName']"));
        firstNameInput.sendKeys(firstName);
        lastNameInput = driver.findElement(By.xpath("//input[@ng-model='lName']"));
        lastNameInput.sendKeys(lastName);
        postCodeInput = driver.findElement(By.xpath("//input[@ng-model='postCd']"));
        postCodeInput.sendKeys(postCode);
        submitCustomerBtn = driver.findElement(By.xpath("//button[@type='submit'][text()='Add Customer']"));
        submitCustomerBtn.click();
        Assert.assertTrue(isAlertPresent(), "Alert isn't displayed");
        Assert.assertTrue(alertMessage.contains("Customer added successfully with customer id :"));
        customerId = alertMessage.substring(alertMessage.length() - 1);
        System.out.println("Customer ID is as following: " + customerId);
        //To check that the newly created customer is displayed:
        viewExistedCustomersBtn = driver.findElement(By.xpath("//button[@ng-click='showCust()']"));
        viewExistedCustomersBtn.click();
        WebElement lastCustomerRecord = driver.findElement(By.xpath("(//*[@ng-repeat='cust in Customers | orderBy:sortType:sortReverse | filter:searchCustomer'])[" + customerId + "]"));
        System.out.println(lastCustomerRecord.getText());
    }

    @Test
    public void scenario2() {
        bankManagerLoginBtn = driver.findElement(By.xpath("//button[@ng-click='manager()']"));
        wait.until(ExpectedConditions.visibilityOf(bankManagerLoginBtn));
        bankManagerLoginBtn.click();
        addCustomerBtn = driver.findElement(By.xpath("//button[@ng-click='addCust()']"));
        addCustomerBtn.click();
        firstNameInput = driver.findElement(By.xpath("//input[@ng-model='fName']"));
        firstNameInput.sendKeys(fName);
        lastNameInput = driver.findElement(By.xpath("//input[@ng-model='lName']"));
        lastNameInput.sendKeys(lName);
        postCodeInput = driver.findElement(By.xpath("//input[@ng-model='postCd']"));
        postCodeInput.sendKeys(PostCode);
        submitCustomerBtn = driver.findElement(By.xpath("//button[@type='submit'][text()='Add Customer']"));
        submitCustomerBtn.click();
        Assert.assertTrue(isAlertPresent(), "Alert isn't displayed");
        Assert.assertTrue(alertMessage.contains("Customer added successfully with customer id :"));
        customerId = alertMessage.substring(alertMessage.length() - 1);
        System.out.println("Customer ID is as following: " + customerId);

        openAccountBtn = driver.findElement(By.xpath("//button[@ng-click='openAccount()']"));
        openAccountBtn.click();
        waitForPageLoad(driver, 3);
        customerDropDown = driver.findElement(By.id("userSelect"));
        customerDropDown.click();
        //selectLastCreatedCustomer = driver.findElement(By.xpath("//option[@value='" + customerId + "']"));
        select = new Select(customerDropDown);
        listSize = select.getOptions().size();
        select.selectByIndex(listSize - 1);
        currencyDropDown = driver.findElement(By.id("currency"));
        currencyDropDown.click();
        selectCurrency = driver.findElement(By.xpath("//option[@value='Dollar']"));
        selectCurrency.click();
        processOpenAccBtn = driver.findElement(By.xpath("//button[@type='submit'][text()='Process']"));
        processOpenAccBtn.click();
        Assert.assertTrue(isAlertPresent(), "Alert isn't displayed");
        Assert.assertTrue(alertMessage.contains("Account created successfully with account Number :"));
        accountNumber = alertMessage.substring(50);
        //accountNumber = alertMessage.substring(alertMessage.length() - 1);
        System.out.println(alertMessage);
        homeBtn = driver.findElement(By.xpath("//button[@class='btn home']"));
        homeBtn.click();
        customerLogin = driver.findElement(By.xpath("//button[@ng-click='customer()']"));
        customerLogin.click();
        customerDropDown = driver.findElement(By.id("userSelect"));
        customerDropDown.click();
        select = new Select(customerDropDown);
        listSize = select.getOptions().size();
        select.selectByIndex(listSize - 1);
        //selectLastCreatedCustomer = driver.findElement(By.xpath("//option[@value='" + customerId + "']"));
        //selectLastCreatedCustomer.click();
        specificCustomerLoginBtn = driver.findElement(By.xpath("//button[@type='submit']"));
        specificCustomerLoginBtn.click();
        accountNumberValue = driver.findElement(By.xpath("//strong[@class='ng-binding'][contains(text(),'" + accountNumber + "')]"));
        Assert.assertTrue(isElementVisible(accountNumberValue), "accountNumberValue is invisible or different value is visible");
    }

    @Test
    public void scenario3() {
        bankManagerLoginBtn = driver.findElement(By.xpath("//button[@ng-click='manager()']"));
        wait.until(ExpectedConditions.visibilityOf(bankManagerLoginBtn));
        bankManagerLoginBtn.click();
        addCustomerBtn = driver.findElement(By.xpath("//button[@ng-click='addCust()']"));
        addCustomerBtn.click();
        firstNameInput = driver.findElement(By.xpath("//input[@ng-model='fName']"));
        firstNameInput.sendKeys(fName);
        lastNameInput = driver.findElement(By.xpath("//input[@ng-model='lName']"));
        lastNameInput.sendKeys(lName);
        postCodeInput = driver.findElement(By.xpath("//input[@ng-model='postCd']"));
        postCodeInput.sendKeys(PostCode);
        submitCustomerBtn = driver.findElement(By.xpath("//button[@type='submit'][text()='Add Customer']"));
        submitCustomerBtn.click();
        Assert.assertTrue(isAlertPresent(), "Alert isn't displayed");
        Assert.assertTrue(alertMessage.contains("Customer added successfully with customer id :"));
        customerId = alertMessage.substring(alertMessage.length() - 1);
        System.out.println("Customer ID is as following: " + customerId);
        openAccountBtn = driver.findElement(By.xpath("//button[@ng-click='openAccount()']"));
        openAccountBtn.click();
        waitForPageLoad(driver, 3);
        customerDropDown = driver.findElement(By.id("userSelect"));
        customerDropDown.click();
        //selectLastCreatedCustomer = driver.findElement(By.xpath("//option[@value='" + customerId + "']"));
        select = new Select(customerDropDown);
        listSize = select.getOptions().size();
        select.selectByIndex(listSize - 1);
        currencyDropDown = driver.findElement(By.id("currency"));
        currencyDropDown.click();
        selectCurrency = driver.findElement(By.xpath("//option[@value='Dollar']"));
        selectCurrency.click();
        processOpenAccBtn = driver.findElement(By.xpath("//button[@type='submit'][text()='Process']"));
        processOpenAccBtn.click();
        Assert.assertTrue(isAlertPresent(), "Alert isn't displayed");
        Assert.assertTrue(alertMessage.contains("Account created successfully with account Number :"));
        accountNumber = alertMessage.substring(50);
        System.out.println(alertMessage);

        //To check that the newly created customer is displayed:
        viewExistedCustomersBtn = driver.findElement(By.xpath("//button[@ng-click='showCust()']"));
        viewExistedCustomersBtn.click();
        WebElement SearchCustomer = driver.findElement(By.xpath("//input[@placeholder='Search Customer']"));
        SearchCustomer.sendKeys(fName);
        WebElement customerRecord = driver.findElement(By.xpath("//*[@ng-repeat='cust in Customers | orderBy:sortType:sortReverse | filter:searchCustomer']"));

        String customerDetails = customerRecord.getText();
        System.out.println("Customer Details are as following: " + customerDetails);
        Assert.assertTrue(customerDetails.contains(fName) && customerDetails.contains(lName) && customerDetails.contains(PostCode) && customerDetails.contains(accountNumber), "Details are not as expected");
    }


    @Test
    public void scenario4() {
        bankManagerLoginBtn = driver.findElement(By.xpath("//button[@ng-click='manager()']"));
        wait.until(ExpectedConditions.visibilityOf(bankManagerLoginBtn));
        bankManagerLoginBtn.click();
        viewExistedCustomersBtn = driver.findElement(By.xpath("//button[@ng-click='showCust()']"));
        viewExistedCustomersBtn.click();
        waitForPageLoad(driver, 3);
        WebElement postalCodeSortLink = driver.findElement(By.xpath("//a[@ng-click=\"sortType = 'postCd'; sortReverse = !sortReverse\"]"));
        postalCodeSortLink.click();
        WebElement postalCodeSortDESC = driver.findElement(By.xpath("//a//span[@ng-show=\"sortType == 'postCd' && sortReverse\"][@class='fa fa-caret-up']"));
        Assert.assertEquals(!isElementVisible(postalCodeSortDESC), true, "The postal code sorting isn't DESC as expected after selecting to sort it");
    }

    @DataProvider
    public Object[][] CustomerTestDataFeed() {
        Object[][] data = new Object[3][3];
        //Object[][] data = new Object[1][1];
        data[0][0] = "FirstN1";
        data[0][1] = "LastN1";
        data[0][2] = "12345";

        data[1][0] = "FirstN2";
        data[1][1] = "LastN2";
        data[1][2] = "23456";

        data[2][0] = "FirstN3";
        data[2][1] = "LastN3";
        data[2][2] = "34567";
        return data;
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            alertMessage = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        }
    }


    public void waitForPageLoad(WebDriver driver, int timeout) {
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    public boolean isElementVisible(WebElement elementLocator) {
        try {
            wait.until(ExpectedConditions.visibilityOf(elementLocator));
            System.out.println("Element matching this locator [ " + elementLocator + " ] is visible");
            return true;
        } catch (Exception e) {
            System.out.println("Element matching this locator [ " + elementLocator + " ] is not visible");
            return false;
        }
    }

}
