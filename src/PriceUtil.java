import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;


public class PriceUtil {

    public static int dayCounter = 0;
    static URL infoCurrentPrice;
    static URL infoHistory;
    static Map<String, Double> history = new LinkedHashMap<>();

    static {
        try {
            infoCurrentPrice = new URL("https://api.coindesk.com/v1/bpi/currentprice/USD.json");
            infoHistory = new URL("https://api.coindesk.com/v1/bpi/historical/close.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    static JSONObject getCurrentPriceInfo() throws Exception {
        try (InputStream in = infoCurrentPrice.openStream()) {
            Files.copy(in, Paths.get("resources\\bitcoinCurrentPrice.json"), StandardCopyOption.REPLACE_EXISTING);
        }
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader("resources\\bitcoinCurrentPrice.json");
        Object obj = parser.parse(reader);
        reader.close();
        return (JSONObject) obj;
    }

    static String getTimeInfo() throws Exception {
        JSONObject time = (JSONObject) getCurrentPriceInfo().get("time");
        return (String) time.get("updated");
    }

    static JSONObject getPricesHistoryFromJSON() throws IOException, ParseException {
//        try (InputStream in = infoHistory.openStream()) {
//            Files.copy(in, Paths.get("resources\\bitcoinHistoryPrice.json"), StandardCopyOption.REPLACE_EXISTING);
//        }
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader("resources\\bitcoinHistoryPrice.json");
        Object obj = parser.parse(reader);
        reader.close();
        return (JSONObject) obj;
    }

    Date getPreviousDay(Calendar date){
        date.setTimeInMillis(date.getTimeInMillis() - 86400000);
        return date.getTime();
    }

    String getFormattedDateOnly(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }

    Double getDailyPriceFromJson(Date date){
        try {
            JSONObject bitCoinHistory = (JSONObject) PriceUtil.getPricesHistoryFromJSON().get("bpi");
            return (Double) bitCoinHistory.get(getFormattedDateOnly(date));
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
            return 0.0;
        }

    }

    void getHistory() {
        Calendar todayDate = new GregorianCalendar(2021, Calendar.MARCH,13);
//        Calendar todayDate = new GregorianCalendar();
        Date dayBefore;
        while (true) {
            dayBefore = getPreviousDay(todayDate);
            Double dayBeforePrice = getDailyPriceFromJson(dayBefore);
            if (dayBeforePrice != null) {
                dayCounter++;
                history.put(getFormattedDateOnly(dayBefore), dayBeforePrice);
            } else {
                break;
            }
        }
    }
}