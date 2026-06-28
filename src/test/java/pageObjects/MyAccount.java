package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MyAccount extends BasePage {

	public MyAccount(WebDriver driver) {
		super(driver);
	}

	// ==========================================
	// LOCATORS (WebElements)
	// ==========================================

	@FindBy(xpath = "//h2[text()='My Account']")
	WebElement msgHeading;
	
	@FindBy(xpath = "//div[@class='list-group']//a[text()='Logout']")
	WebElement clkLogout;

	// ==========================================
	// ACTION METHODS
	// ==========================================

	public boolean accountPageExist() {
		try {
			return (msgHeading.isDisplayed());
		} catch (Exception e) {
			return false;

		}
	}
	
	public void clickLogout() {
		clkLogout.click();
	}

}
