package com.sm.config;

import java.io.IOException;
import java.nio.file.Files;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

	@Bean
	WebDriver webDriver() throws IOException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		String tempProfileDir = Files.createTempDirectory("chrome-profile-").toString();
		options.addArguments("--user-data-dir=" + tempProfileDir);
	    return new ChromeDriver();
	}
}

