package ai.makeitright.utilities.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "auctions")
public class AuctionData {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String marka;
    @DatabaseField
    private String model;
    @DatabaseField
    private String rokProdukcji;
    @DatabaseField
    private String numerRejestracyjny;
    @DatabaseField
    private String vin;
    @DatabaseField
    private String rodzajPaliwa;
    @DatabaseField
    private String klasaEuro;
    @DatabaseField
    private String kluczyki;
    @DatabaseField
    private String dowodRejestracyjny;
    @DatabaseField
    private String kartaPojazdu;
    @DatabaseField
    private Long przebieg;
    @DatabaseField
    private Long cena;
    @DatabaseField
    private String pdfUrl;
    @DatabaseField
    private String wyposazenie;
    @DatabaseField
    private Timestamp dataWyszukania;
    @DatabaseField
    private Timestamp doKoncaAukcji;
    @DatabaseField
    private String zrodlo;
    @DatabaseField
    private String typAukcji;

    public static AuctionData crateAuctionDataObjectFromJSoupDocument(Document document, String urlOfAuction) throws ParseException {
        Element divAC = document.selectFirst("#strona-opis");
        AuctionData ad = new AuctionData();
        ad.setId(urlOfAuction);

        String markaString;
        Element markaElement = divAC.selectFirst("div.fl.lewo div.parametry div.fl.k:contains(Marka:)");
        if (markaElement != null) {
            markaString = markaElement.nextElementSibling().text();
        } else {
            markaString = "0";
        }
        ad.setMarka(markaString);

        ad.setModel(divAC.selectFirst("div.fl.lewo div.parametry div.fl.k:contains(Opis modelu:)").nextElementSibling().text());

        String withR = divAC.selectFirst("div.fl.lewo div.parametry div.fl.k:contains(Rok produkcji:)").nextElementSibling().text();
        String rokProdukcji = withR.replace(" r.", "");
        ad.setRokProdukcji(rokProdukcji);

        ad.setNumerRejestracyjny("");

        //todo: ac.selectFirst("div.fl.lewo div.parametry div.fl.k:contains(VIN:)").nextElementSibling().text(),
        ad.setVin("");

        ad.setRodzajPaliwa("");

        ad.setKlasaEuro("");

        ad.setKluczyki("");

        ad.setDowodRejestracyjny("");

        ad.setKartaPojazdu("");

        String przebiegString;
        Element przebiegElement = divAC.selectFirst("div.fl.lewo div.parametry div.fl.k:contains(Przebieg:)");
        if (przebiegElement != null) {
            String przebiegWithKm = przebiegElement.nextElementSibling().text();
            przebiegString = przebiegWithKm.replace(" km", "");
        } else {
            przebiegString = "0";
        }
        ad.setPrzebieg(Long.valueOf(przebiegString));

        String cenaWithSpacesAndZl;
        Element najwyzszaOferta = divAC.selectFirst("#najwyzsza-oferta");
        if (najwyzszaOferta != null) {
            if (divAC.selectFirst("#najwyzsza-oferta").ownText().equals("brak ofert")) {
                cenaWithSpacesAndZl = divAC.selectFirst("#kwoty div.fl:nth-child(2)").ownText();
            } else {
                cenaWithSpacesAndZl = divAC.selectFirst("#najwyzsza-oferta").ownText();
            }
            String cena = cenaWithSpacesAndZl
                    .replaceAll("\\s+", "")
                    .replaceAll("([,00])\\w+[^\\x00-\\x7F]+", "");
            ad.setCena(Long.valueOf(cena));
        } else {
            ad.setCena(0L);
        }
        ad.setPdfUrl("");

        ad.setWyposazenie(divAC.selectFirst("div.fl.lewo div.parametry:nth-of-type(4) div").ownText());

        Timestamp dataWyszukaniaTimestamp = new Timestamp(System.currentTimeMillis());
        ad.setDataWyszukania(dataWyszukaniaTimestamp);

        String czasTrwania = divAC.selectFirst("div.fl.koniec-aukcji").ownText();
        Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})");
        Matcher matcher = pattern.matcher(czasTrwania);
        String koniecAukcji = null;
        if (matcher.find()) {
            koniecAukcji = matcher.group();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedDate = dateFormat.parse(koniecAukcji);
        Timestamp koniecAukcjiTimestamp = new java.sql.Timestamp(parsedDate.getTime());
        ad.setDoKoncaAukcji(koniecAukcjiTimestamp);

        ad.setZrodlo(System.getProperty("inputParameters.title"));

        ad.setTypAukcji("licytacja");

        return ad;
    }

}