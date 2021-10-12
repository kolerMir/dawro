package ai.makeitright.utilities.scraper;

import ai.makeitright.utilities.DriverConfig;
import ai.makeitright.utilities.db.AuctionData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static ai.makeitright.utilities.db.AuctionData.crateAuctionDataObjectFromJSoupDocument;

public class Scraper extends DriverConfig {

    public static ArrayList<String> scrapeUrlsOfAuctions(ArrayList<String> arrayListOfPageSources) {
        List<String> hrefsOfAllAuctions = scrapePartialLinksToAuctionsDetials(arrayListOfPageSources, "div.aukcja-lista div.fl.przycisk.zielony.strzalka:contains(LICYTUJ)", "div.aukcja-lista a");
        ArrayList<String> urlsOfSpecificAuctions = new ArrayList<>();
        for (String hrefOfAuction : hrefsOfAllAuctions) {
            urlsOfSpecificAuctions.add("https://dawro.pl" + hrefOfAuction);
        }
        return urlsOfSpecificAuctions;
    }

    public static ArrayList<AuctionData> scrapeAuctions(final ArrayList<String> urlsOfAllAuctions) throws InterruptedException, ParseException {
        System.out.println("------------------------------------------------------");
        System.out.println("Quantity of urls of all auctions: " + urlsOfAllAuctions.size());
        System.out.println("");
        ArrayList<AuctionData> auctionDatas = new ArrayList<>();
        for (String urlOfAuction : urlsOfAllAuctions) {
            System.out.println("Downloading url: " + urlOfAuction);
            driver.navigate().to(urlOfAuction);
            Thread.sleep(1000);
            Document document = Jsoup.parse(driver.getPageSource());
            AuctionData ad = crateAuctionDataObjectFromJSoupDocument(document, urlOfAuction);
            auctionDatas.add(ad);
        }
        return auctionDatas;
    }

    public static List<String> scrapePartialLinksToAuctionsDetials(final ArrayList<String> htmlPagesAsString,
                                                                   final String selectForActiveOne,
                                                                   final String selectForAElement
    ) {
        List<String> finalListOfPartialLinks = new ArrayList<>();
        for (String htmlPageAsString : htmlPagesAsString) {
            Document parsedHtmlPage = Jsoup.parse(htmlPageAsString);
            if (!parsedHtmlPage.select(".tresc.komunikat.informacja").hasText()) {
                Elements activeOnes = parsedHtmlPage.select(selectForActiveOne).parents();
                Elements aTags = activeOnes.select(selectForAElement);
                List<String> temporarylistOfPartialLinks = aTags.eachAttr("href");
                finalListOfPartialLinks.addAll(temporarylistOfPartialLinks);
            }
        }
        return finalListOfPartialLinks;
    }

    /**
     * USE WITH CAUTION!
     * don't download pdf files if not required
     */
    static void downloadPdfFile(final String aTagXpath) {
        //hardcode for https://makeitright.atlassian.net/browse/PC-7
        System.setProperty("inputParameters.downloadFiles", "false");
        if (System.getProperty("inputParameters.downloadFiles").equals("true")) {
            String pdfUrl = driver.findElement(By.xpath(aTagXpath)).getAttribute("href");
            driver.navigate().to(pdfUrl);
        } else {
            System.out.println("downloading files is turned off");
        }
    }

}
