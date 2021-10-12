package ai.makeitright.utilities.crawler;

import ai.makeitright.utilities.DriverConfig;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Crawler extends DriverConfig {

    public static ArrayList<String> crawl() throws InterruptedException {
        crawlToPagesWithSearchResults();
        Thread.sleep(3000);
        ArrayList<String> arrayListOfPageSources = new ArrayList<>();
        arrayListOfPageSources.add(driver.getPageSource());
        return arrayListOfPageSources;
    }

    public static void crawlToPagesWithSearchResults() throws InterruptedException {
        Thread.sleep(3000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement showList = driver.findElement(By.xpath("//div[@class='fr wyswietlanie']//a[contains(@href,',lista')]/img"));
        showList.click();
        Thread.sleep(2500);
        WebElement wszystkieAukcje = driver.findElement(By.xpath("//a[contains(text(),'wszystkie aukcje')]"));
        js.executeScript("arguments[0].scrollIntoView();", wszystkieAukcje);
        wszystkieAukcje.click();
    }

    public static void logIn() throws InterruptedException, IOException {
        String uri = System.getProperty("inputParameters.startPage");
        driver.navigate().to(uri);
        //https://makeitright.atlassian.net/browse/PC-34
//        takeScreenshot("przezKliknieciemLOGOWANIE");
//        driver.findElement(By.linkText("LOGOWANIE")).click();
//        Thread.sleep(3000);
//
//        driver.findElement(By.cssSelector(".pole:nth-child(2) > input")).click();
//        driver.findElement(By.cssSelector(".pole:nth-child(2) > input")).sendKeys(System.getProperty("inputParameters.user"));
//
//        driver.findElement(By.name("haslo")).sendKeys(System.getProperty("inputParameters.password"));
//
//        driver.findElement(By.cssSelector(".przycisk:nth-child(4) > input")).click();
//        System.out.println("Zaloguj button clicked");
//        Thread.sleep(1000);
        Thread.sleep(3000);
    }

    static boolean webElementExists(By by) {
        return !driver.findElements(by).isEmpty();
    }

    private static void takeScreenshot(final String filename) throws IOException {
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File justAfterOpeningPage = takesScreenshot.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(justAfterOpeningPage,
                new File(System.getProperty("SCREENSHOTS_PATH") + System.getProperty("file.separator") + filename + ".png")
        );
    }

}
