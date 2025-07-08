package com.sm.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

	@Bean
	WebDriver webDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
	    return new ChromeDriver();
	}
}

