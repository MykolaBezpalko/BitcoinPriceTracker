import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
        try (InputStream in = infoHistory.openStream()) {
            Files.copy(in, Paths.get("resources\\bitcoinHistoryPrice.json"), StandardCopyOption.REPLACE_EXISTING);
        }
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader("resources\\bitcoinHistoryPrice.json");
        Object obj = parser.parse(reader);
        reader.close();
        return (JSONObject) obj;
    }

    static void getHistory() throws IOException, ParseException {
        int milliSecondOfDay = 86400000;
        JSONObject bitCoinHistory = (JSONObject) Logic.getPricesHistoryFromJSON().get("bpi");
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());

        Date bufferDate;
        String sDate;
        Calendar calendar = new GregorianCalendar();
        while(true){
            calendar.setTimeInMillis(calendar.getTimeInMillis() - milliSecondOfDay);
            bufferDate = calendar.getTime();
            sDate = formatter.format(bufferDate);
            Double prices = (Double) bitCoinHistory.get(sDate);
            if(prices != null){
                dayCounter++;
                history.put(sDate, prices);
            }
            if(prices == null){
                return;
            }
        }

    }
    
}
