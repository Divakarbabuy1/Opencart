package testCases;

import org.testng.annotations.Test;
import org.testng.Assert;

import pageObjects.AccountRegistrationPage;
import pageObjects.HomePage;
import testBase.BaseClass;

/**
 * Test Case: TC001_RegistrationTest
 * Verifies the end-to-end user registration workflow using dynamic test data.
 */
public class TC001_RegistrationTest extends BaseClass {

	@Test(groups = {"Sanity", "Regression"})
	public void verify_registration() {

		try {
			logger.info("###### Starting TC001_RegistrationTest #########");

			// Navigate to Registration Step
			logger.info("Moving to Home Page");
			HomePage hp = new HomePage(getDriver());
			logger.info("Clicking on My Account");
			hp.clickMyAccount();
			logger.info("Clicking on Register");
			hp.clickRegister();

			// Populate Registration Fields
			logger.info("Navigating to Registration page");
			AccountRegistrationPage arp = new AccountRegistrationPage(getDriver());

			logger.info("Providing dynamic user details");
			arp.setFirstName(randomString().toUpperCase());
			arp.setLastName(randomString().toUpperCase());
			arp.setEmail(randomEmail());
			arp.setTelephone(randomPhoneNumber());

			String psw = randomAlphaNumeric();
			arp.setPassword(psw);
			arp.setConfirmPassword(psw);
			arp.clickAgree();
			arp.clickContinue();

			// Validate Outcomes
			logger.info("Verifying success message");
			String successMsg = arp.getSuccessMessage();

			// Native TestNG Assertion (Actual, Expected)
			Assert.assertEquals(successMsg, "Your Account Has Been Created!");
			logger.info("TC001_RegistrationTest Passed Successfully");

		} catch (Throwable e) {
			logger.error("Failed TC001_RegistrationTest");
			logger.error("Error details: " + e.getMessage());
			// Native TestNG Fail step
			Assert.fail("Test failed due to: " + e.getMessage());
		}

		logger.info("###### End of the TC001_RegistrationTest #########");
	}
}