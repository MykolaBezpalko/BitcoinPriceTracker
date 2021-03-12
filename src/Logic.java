import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;



public class Logic {

    public static int dayCounter = 0;
    static URL infoCurrentPrice;
    static URL infoHistory;
    static final int dayMilliseconds = 86400000;
    static Map<String, Double> historyMap = new LinkedHashMap<>();

    static {
        try {
            infoCurrentPrice = new URL("https://api.coindesk.com/v1/bpi/currentprice/USD.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            infoHistory = new URL("https://api.coindesk.com/v1/bpi/historical/close.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    static JSONObject getCurrentPriceInfo() throws Exception {

        try (InputStream in = infoCurrentPrice.openStream()) {
            Files.copy(in, Paths.get("resources\\bitcoinCurrentPrice.json"),
                    StandardCopyOption.REPLACE_EXISTING);
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

    static JSONObject getPricesHistory() throws Exception {
        try (InputStream in = infoHistory.openStream()) {
            Files.copy(in, Paths.get("resources\\bitcoinHistoryPrice.json"),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader("resources\\bitcoinHistoryPrice.json");
        Object obj = parser.parse(reader);
        reader.close();
        return (JSONObject) obj;
    }

    static void getHistory() throws Exception {
        JSONObject bpiHistory = (JSONObject) Logic.getPricesHistory().get("bpi");

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());

        Date bufferDate;
        String sDate;
        Calendar calendar = new GregorianCalendar();
        while(true){
            calendar.setTimeInMillis(calendar.getTimeInMillis() - dayMilliseconds);
            bufferDate = calendar.getTime();
            sDate = formatter.format(bufferDate);
            Double prices = (Double) bpiHistory.get(sDate);
            if(prices != null){
                dayCounter++;
                historyMap.put(sDate, prices);
            }
            if(prices == null){
                return;
            }
        }

    }
}
