package com.lifeboxBackend.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.util.Properties;

public class BasePage {

    public static Properties prop;
    public WebDriver driver;
    public WebDriverWait wait;


    public BasePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }


    public static Properties prop() {

        try {
            prop = new Properties();
            FileInputStream ip = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/com/lifeboxBackend/lifeboxBackend/config/config.properties");
            prop.load(ip);
            return prop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
