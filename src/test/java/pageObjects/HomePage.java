package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object class representing the application landing page.
 * Provides entry points for registration, login, and account management.
 */
public class HomePage extends BasePage {

	// Constructor passing the driver reference to the BasePage template
	public HomePage(WebDriver driver) {
		super(driver);
	}

	// ==========================================
	// LOCATORS (WebElements)
	// ==========================================

	@FindBy(xpath = "//a[@title='My Account']")
	WebElement clkmyAccount;

	@FindBy(xpath = "//ul[@class='dropdown-menu dropdown-menu-right']//a[text()='Register']")
	WebElement clkRegister;
	
	@FindBy(xpath = "//ul[@class='dropdown-menu dropdown-menu-right']//a[text()='Login']")
	WebElement clkLogin;

	// ==========================================
	// ACTION METHODS
	// ==========================================

	public void clickMyAccount() {
		clkmyAccount.click();
	}

	public void clickRegister() {
		clkRegister.click();
	}
	
	public void clickLogin() {
		clkLogin.click();
	}

}
