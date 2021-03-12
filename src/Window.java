import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Map;

public class Window extends JPanel {
    static int square = 20;
    static int height = square * 30;
    static int width = (square * Logic.dayCounter) + (square * 20);

    static void openWindow(Window window) {
        JFrame frame = new JFrame("Bitcoin Price Viev");
        frame.add(window);
        frame.setSize(width, height);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Window() {
        openWindow(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getBackground(g);
        try {
            drawCurrentPrice(g);
        } catch (Exception e) {
            System.out.println("Got some problem");
        }
        drawGraphicBody(g);
        drawDates(g);
        drawGraphic(g);
    }

    public void getBackground(Graphics g) {
        Color backgroundColor = new Color(0, 0, 0, 248);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
    }

    public void drawCurrentPrice(Graphics g) throws Exception {
        JSONObject bpiCurrent = (JSONObject) Logic.getCurrentPriceInfo().get("bpi");
        JSONObject usd = (JSONObject) bpiCurrent.get("USD");
        double price = (double) usd.get("rate_float");
        Color customColor = new Color(137, 6, 6);
        g.setColor(customColor);
        g.drawString("Today: " + Logic.getTimeInfo() + " --->  " + price + " USD", square * 5, square * 4);
    }

    public void drawGraphicBody(Graphics g) {
        Color lineColor = new Color(175, 175, 175);
        g.setColor(lineColor);
        g.drawLine( square * 5, square * 5,
                    square * 5, height - (4 * square));

        g.drawLine( square * 5, height - (4 * square),
                    width - (5 * square) - 10 * square, height - (4 * square));

        int counter = 0;
        double growth = 63.0;
        for (int y = square * 6; y < height - (4 * square); y += square) {
            g.drawOval(5 * square - 3, y, 6, 0);

            if (counter % 2 == 0) {
                g.drawOval(5 * square - 9, y, 18, 0);
                g.drawString((growth) + "k $", 5 * square - 50, y - 5);
                growth -= 1;
            }
            counter++;
        }
    }

    public void drawDates(Graphics g) {
        int counter = 0;
        Iterator<Map.Entry<String, Double>> mapIterator = Logic.historyMap.entrySet().iterator();
        for (int x = square * 6; x < width - (6 * square) - 10 * square; x += square) {

            g.drawOval(x, height - (4 * square) - 3, 0, 6);
            Map.Entry<String, Double> next = mapIterator.next();

            if (counter % 2 == 0) {
                Font font = new Font(null, Font.PLAIN, 10);

                AffineTransform affineTransform = new AffineTransform();
                affineTransform.rotate(Math.toRadians(-40), 0, 0);

                Font rotatedFont = font.deriveFont(affineTransform);
                g.setFont(rotatedFont);

                g.drawString(next.getKey(), x - 40, height - (4 * square) + 40);
                g.drawOval(x, height - (4 * square) - 9, 0, 18);
            }
            counter++;
        }
    }

    public void drawGraphic(Graphics g) {
        Iterator<Map.Entry<String, Double>> mapIterator = Logic.historyMap.entrySet().iterator();
        int zeroX = square * 6;
        int zeroY = height - (square * 4);
        int endX = square * 6;
        int endY = height - (square * 4);
        double growth = 43000.00;
        int steps = 0;
        Color customColor = new Color(137, 6, 6);
        g.setColor(customColor);

        while (mapIterator.hasNext()) {
            Map.Entry<String, Double> next = mapIterator.next();
            Double price = next.getValue();
            while (growth < price) {
                growth += 1000;
                zeroY -= square;
            }
            if (steps == 0) {
                g.drawLine(endX, endY, endX, endY);
            } else
                g.drawLine(zeroX, zeroY, endX, endY);
            steps++;
            endY = zeroY;
            endX = zeroX;
            zeroX += square;
            zeroY = height - (square * 4);
            growth = 43000.00;
        }
    }
}



