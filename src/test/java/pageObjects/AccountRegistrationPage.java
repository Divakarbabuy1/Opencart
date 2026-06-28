package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object class representing the Account Registration page.
 * Handles locators and user interactions for user onboarding.
 */
public class AccountRegistrationPage extends BasePage {
    // Constructor passing the driver reference to the BasePage template
    public AccountRegistrationPage(WebDriver driver) {
        super(driver);
    }

    // ==========================================
    // LOCATORS (WebElements)
    // ==========================================

    @FindBy(xpath = "//input[@id='input-firstname']")
    WebElement txtFirstName;

    @FindBy(xpath = "//input[@id='input-lastname']")
    WebElement txtLastName;

    @FindBy(xpath = "//input[@id='input-email']")
    WebElement txtEmail;

    @FindBy(xpath = "//input[@id='input-telephone']")
    WebElement txtTelephone;

    @FindBy(xpath = "//input[@id='input-password']")
    WebElement txtPassword;

    @FindBy(xpath = "//input[@id='input-confirm']")
    WebElement txtConfirmPassword;

    @FindBy(xpath = "//input[@name='agree']")
    WebElement btnclickPolicy;

    @FindBy(xpath = "//input[@value='Continue']")
    WebElement btnclickContinue;

    @FindBy(xpath = "//h1[text()='Your Account Has Been Created!']")
    WebElement successMessage;

    // ==========================================
    // ACTION METHODS
    // ==========================================

    public void setFirstName(String firstName) {
        txtFirstName.sendKeys(firstName);
    }

    public void setLastName(String lastName) {
        txtLastName.sendKeys(lastName);
    }

    public void setEmail(String email) {
        txtEmail.sendKeys(email);
    }

    public void setTelephone(String telephone) {
        txtTelephone.sendKeys(telephone);
    }

    public void setPassword(String password) {
        txtPassword.sendKeys(password);
    }

    public void setConfirmPassword(String confirmPassword) {
        txtConfirmPassword.sendKeys(confirmPassword);
    }

    public void clickAgree() {
        btnclickPolicy.click();
    }

    public void clickContinue() {
        btnclickContinue.click();
    }

    /**
     * Captures the confirmation message displayed post-registration.
     * @return String confirmation message or error trace message if execution fails.
     */

    public String getSuccessMessage() {
        try {
            return successMessage.getText();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
