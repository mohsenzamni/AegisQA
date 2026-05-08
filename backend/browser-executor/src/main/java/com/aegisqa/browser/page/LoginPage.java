package com.aegisqa.browser.page;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Page Object for the ACS admin login screen.
 * Navigates to the login URL, fills credentials, submits the form, and waits
 * for the authenticated landing page to load.
 */
@Slf4j
public class LoginPage extends PageObject {

    private static final String INPUT_USERNAME =
            "[name='username'], [name='email'], [data-testid='username'], #username, input[type='text']";
    private static final String INPUT_PASSWORD =
            "[name='password'], [data-testid='password'], #password, input[type='password']";
    private static final String BTN_SUBMIT =
            "button[type='submit'], button:has-text('Login'), button:has-text('Log In'), button:has-text('Sign In')";

    private final String loginUrl;

    public LoginPage(Page page, String loginUrl) {
        super(page);
        this.loginUrl = loginUrl;
    }

    /**
     * Navigate to the login page, enter credentials, and submit the form.
     *
     * @param username the login username or email
     * @param password the login password
     */
    public void login(String username, String password) {
        log.info("Navigating to login page: {}", loginUrl);
        page.navigate(loginUrl);
        waitForIdle();

        log.info("Filling login credentials");
        fill(INPUT_USERNAME, username);
        fill(INPUT_PASSWORD, password);

        log.info("Submitting login form");
        click(BTN_SUBMIT);
        waitForIdle();

        screenshot("login_result");
    }
}
