package com.aegisqa.browser.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Playwright browser lifecycle.
 */
@Slf4j
@Configuration
public class PlaywrightConfig {

    @Value("${aegisqa.browser.headless:true}")
    private boolean headless;

    @Value("${aegisqa.browser.timeout-ms:30000}")
    private int timeoutMs;

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        log.info("Initializing Playwright");
        return Playwright.create();
    }

    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        log.info("Launching Chromium browser (headless={})", headless);
        return playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(headless)
        );
    }
}
