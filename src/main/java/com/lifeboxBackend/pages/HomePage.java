package com.lifeboxBackend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage extends BasePage {

    private static final String packageButton = "//a[@class='primary-action btn ng-binding']";


    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void goToPackagePage() {

        driver.findElement(By.xpath(packageButton)).clear();

    }

}
