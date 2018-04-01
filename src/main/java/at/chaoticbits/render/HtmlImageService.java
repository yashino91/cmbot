package at.chaoticbits.render;

import at.chaoticbits.coinmarket.CurrencyDetails;
import at.chaoticbits.config.DecimalFormatter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

public class HtmlImageService {


    private static volatile HtmlImageService instance;

    private static final TemplateEngine templateEngine = new TemplateEngine();


    /**
     * Singleton
     *
     * @return Return the instance of this class
     */
    public static HtmlImageService getInstance() {
        HtmlImageService currentInstance;

        if (instance == null) {

            synchronized (HtmlImageService.class) {
                if (instance == null)
                    instance = new HtmlImageService();

                currentInstance = instance;
            }

        } else
            currentInstance = instance;

        return currentInstance;
    }


    /**
     * Initialize TemplateResolver and TemplateEngine
     */
    private HtmlImageService() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.setTemplateResolver(templateResolver);
    }


    /**
     * Generates the rendered HTML with the given currency details
     * and converts it into an image InputStream
     * @param currencyDetails holding information about a crypto currency
     * @return InputStream containing information about the rendered image
     */
    public InputStream generateCryptoDetailsImage(CurrencyDetails currencyDetails) throws IllegalStateException{
        Context context = new Context(Locale.forLanguageTag("de-AT"));

        context.setVariable("currencyDetails", currencyDetails);
        context.setVariable("DecimalFormatter", DecimalFormatter.getInstance());
        context.setVariable("changeColors", getChangePercentageColor(currencyDetails));

        String html = templateEngine.process("html/currency-details.html", context);

        try {

            InputStream htmlInputStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));

            // create a w3c document of the generated html input stream
            DocumentBuilderFactory b = DocumentBuilderFactory.newInstance();
            b.setNamespaceAware(false);
            DocumentBuilder db = b.newDocumentBuilder();
            Document doc = db.parse(htmlInputStream);

            // write image into output stream
            Java2DRenderer render = new Java2DRenderer(doc, 1800);
            BufferedImage image = render.getImage();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os );

            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("Error writing Image: " + e.getMessage());
        }
    }


    /**
     * Populates a Map of colors according to negative and positive percentages
     * @param currencyDetails holding information about a crypto currency
     * @return Map where the key is the name of the percentage and the value the color code.
     */
    private Map<String, String> getChangePercentageColor(CurrencyDetails currencyDetails) {


        Map<String, BigDecimal> changesPositive = new HashMap<>();
        Map<String, BigDecimal> changesNegative = new HashMap<>();
        Map<String, String> colors = new HashMap<>();

        if (currencyDetails.getChange1h() == null)
            colors.put("change1h", "#757575");
        else {
            if (currencyDetails.getChange1h().compareTo(BigDecimal.ZERO) > 0)
                changesPositive.put("change1h", currencyDetails.getChange1h());
            else
                changesNegative.put("change1h", currencyDetails.getChange1h());
        }

        if (currencyDetails.getChange24h() == null)
            colors.put("change24h", "#757575");
        else {
            if (currencyDetails.getChange24h().compareTo(BigDecimal.ZERO) > 0)
                changesPositive.put("change24h", currencyDetails.getChange24h());
            else
                changesNegative.put("change24h", currencyDetails.getChange24h());
        }

        if (currencyDetails.getChange7d() == null)
            colors.put("change7d", "#757575");
        else {
            if (currencyDetails.getChange7d().compareTo(BigDecimal.ZERO) > 0)
                changesPositive.put("change7d", currencyDetails.getChange7d());
            else
                changesNegative.put("change7d", currencyDetails.getChange7d());
        }


        colors.putAll(getColors(changesPositive, true));
        colors.putAll(getColors(changesNegative, false));

        return colors;
    }

    /**
     * Populates a Map with red or green colors
     * depending on the positive flag and the containing values in the map
     * @param map containing percentage values
     * @param positive determines if the values in the map are positive or negative
     * @return Map where the key is the name of the percentage and the value the color code.
     */
    private Map<String, String> getColors(Map<String, BigDecimal> map, boolean positive) {

        Map<String, String> colors = new HashMap<>();

        String min, middle, max;

        min = getMinValue(map);
        if (min == null)
            return colors;

        colors.put(min, positive ? "#4CAF50" : "#BF360C");
        map.remove(min);

        middle = getMinValue(map);
        if (middle == null)
            return colors;

        colors.put(middle, positive ? "#388E3C" : "#E64A19");
        map.remove(middle);

        max = getMinValue(map);
        if (max == null)
            return colors;

        colors.put(max, positive ? "#2E7D32" : "#FF7043");
        return colors;
    }


    /**
     * Determines the minimum value in the given Map
     * @param map containing percentage values
     * @return name of the minimum value in the given map
     */
    private String getMinValue(Map<String, BigDecimal> map)  {
        Map.Entry<String, BigDecimal> min = null;
        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            if (min == null || min.getValue().compareTo(entry.getValue()) > 0)
                min = entry;
        }

        if (min == null)
            return null;

        return min.getKey();
    }
}
