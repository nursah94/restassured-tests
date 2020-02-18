package com.lifeboxBackend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WelcomePage extends BasePage {

    private static final String emailTextBox = "//input[@placeholder='E-mail']";
    private static final String passwordTextBox = "//input[@placeholder='Password']";
    private static final String loginButton = "//button[@class='primary-action ng-binding ng-isolate-scope']";
    private static final String testPageTitle = "lifebox";


    public WelcomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }


    public void goToHomePage() {

        driver.get(prop().getProperty("url"));
    }

    public boolean checkOfOnPage() {
        return driver.getTitle().contentEquals(testPageTitle);
    }

    public void goToLoginPage() {

        driver.get(prop().getProperty("url"));
        driver.findElement(By.xpath(emailTextBox)).sendKeys(prop().getProperty("msisdn")); // Properties dosyası içine ekle.
        driver.findElement(By.xpath(passwordTextBox)).sendKeys(prop().getProperty("password"));
        driver.findElement(By.xpath(loginButton)).click();
    }
}
