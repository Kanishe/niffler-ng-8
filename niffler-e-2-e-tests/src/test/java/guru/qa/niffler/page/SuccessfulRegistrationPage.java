package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;

public class SuccessfulRegistrationPage {
    private final SelenideElement congratsField = $x("//p[contains(text(), \"Congratulations!\")]");
    private final SelenideElement signInButton = $x("//a[contains(text(), \"Sign in\")]");

    public SuccessfulRegistrationPage checkMessageOfSuccessfulRegistration(String message) {
        congratsField.shouldHave(text(message));
        return this;
    }

    public LoginPage signIn() {
        signInButton.click();
        return new LoginPage();
    }
}
