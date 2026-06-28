package testCases;

import org.testng.annotations.Test;
import org.testng.Assert;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccount;
import testBase.BaseClass;

/**
 * Test Case: TC002_LoginTest
 * Verifies successful user login using credentials from configuration properties.
 */
public class TC002_LoginTest extends BaseClass {

	@Test(groups = {"Smoke", "Regression"})
	public void verify_login() {

		try {
			logger.info("###### Starting TC002_LoginTest #########");

			// Navigate to Login Screen
			HomePage hp = new HomePage(getDriver());
			hp.clickMyAccount();
			hp.clickLogin();

			// Populate Login Credentials
			logger.info("Entering user credentials");
			LoginPage lp = new LoginPage(getDriver());
			lp.setEmail(prop.getProperty("email"));
			lp.setPassword(prop.getProperty("psw"));
			lp.clickLogin();

			// Validate Dashboard Landing Page exists
			logger.info("Verifying My Account dashboard presence");
			MyAccount mac = new MyAccount(getDriver());
			boolean labelPresent = mac.accountPageExist();

			// Native TestNG Assertion
			Assert.assertTrue(labelPresent, "My Account page validation failed.");
			logger.info("TC002_LoginTest Passed Successfully");

		} catch (Exception e) {
			logger.error("Failed TC002_LoginTest due to exception: " + e.getMessage());
			// Native TestNG Fail step
			Assert.fail("Test failed due to exception: " + e.getMessage());
		}

		logger.info("###### End of the TC002_LoginTest #########");
	}
}