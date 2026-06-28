package testCases;

import org.testng.annotations.Test;
import org.testng.Assert;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccount;
import testBase.BaseClass;
import utilities.DataProviders;

/**
 * Test Case: TC003_LoginTestDDT
 * Data-Driven Test validating both positive and negative login combinations
 * driven from an external data provider matrix.
 */
public class TC003_LoginTestDDT extends BaseClass {

	@Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups = "Regression")
	public void verify_ddtLogin(String eml, String psw, String exp) {

		logger.info("###### Starting Data Set -> Email: " + eml + " | Expected Status: " + exp + " #########");

		try {
			// Navigate to Login Screen
			HomePage hp = new HomePage(getDriver());
			hp.clickMyAccount();
			hp.clickLogin();
			logger.info("Navigated to the login page");

			// Input Data Matrix Credentials
			LoginPage lp = new LoginPage(getDriver());
			lp.setEmail(eml);
			lp.setPassword(psw);
			lp.clickLogin();
			logger.info("Provided credentials from data provider row");

			// Check Dashboard Visibility
			MyAccount mac = new MyAccount(getDriver());
			boolean labelPresent = mac.accountPageExist();
			logger.info("Verifying My Account dashboard presence status: " + labelPresent);

			// ========================================================
			// SCENARIO 1: EXPECTED VALID DATA SET
			// ========================================================
			if (exp.equalsIgnoreCase("valid")) {
				if (labelPresent) {
					logger.info("Login successful with valid credentials. Proceeding to logout.");
					mac.clickLogout();
					Assert.assertTrue(true);
				} else {
					logger.error("Login failed unexpectedly with valid credentials.");
					Assert.fail("Expected successful login, but dashboard was not displayed.");
				}
			}

			// ========================================================
			// SCENARIO 2: EXPECTED INVALID DATA SET
			// ========================================================
			if (exp.equalsIgnoreCase("Invalid")) {
				if (labelPresent) {
					logger.error("Security alert: Login succeeded unexpectedly with invalid credentials.");
					mac.clickLogout();
					Assert.fail("Expected login failure for invalid credentials, but dashboard was accessible.");
				} else {
					logger.info("Login correctly blocked for invalid credentials.");
					Assert.assertTrue(true);
				}
			}

		} catch (Exception e) {
			logger.error("Exception encountered during data row execution: " + e.getMessage());
			Assert.fail("Test execution interrupted by an unhandled exception: " + e.getMessage());
		}

		logger.info("###### Ending Data Set -> Email: " + eml + " #########");
	}
}