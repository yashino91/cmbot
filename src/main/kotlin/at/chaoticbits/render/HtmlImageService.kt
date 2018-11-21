package at.chaoticbits.render

import at.chaoticbits.currencydetails.CurrencyDetails
import at.chaoticbits.config.DecimalFormatter
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.xhtmlrenderer.swing.Java2DRenderer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory


object HtmlImageService {

    private val templateEngine = TemplateEngine()


    /**
     * Initialize TemplateResolver and TemplateEngine
     */
    init {

        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.prefix = "/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = HTML
        templateResolver.characterEncoding = "UTF-8"

        templateEngine.setTemplateResolver(templateResolver)
    }


    /**
     * Generates the rendered HTML with the given currency details
     * and converts it into an image InputStream
     *
     * @param currencyDetails [CurrencyDetails] Holding information about a crypto currency
     * @return [InputStream] Containing information about the rendered image
     */
    @Throws(IllegalStateException::class)
    fun generateCryptoDetailsImage(currencyDetails: CurrencyDetails): InputStream {
        val context = Context(Locale.forLanguageTag("de-AT"))

        val classloader = Thread.currentThread().contextClassLoader

        context.setVariable("currencyDetails", currencyDetails)
        context.setVariable("DecimalFormatter", DecimalFormatter)
        context.setVariable("change24hColor", getPercentageColor(currencyDetails.change24h))
        context.setVariable("bootstrapCss", classloader.getResource("css/bootstrap.min.css"))

        val html = templateEngine.process("html/currency-details.html", context)


        try {
            val htmlInputStream = ByteArrayInputStream(html.toByteArray(StandardCharsets.UTF_8))

            // create a w3c document of the generated html input stream
            val b = DocumentBuilderFactory.newInstance()
            b.isNamespaceAware = false
            val db = b.newDocumentBuilder()
            val doc = db.parse(htmlInputStream)

            // write image into output stream
            val os = ByteArrayOutputStream()
            ImageIO.write(Java2DRenderer(doc, 1800).image, "png", os)
            return ByteArrayInputStream(os.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Error writing Image: " + e.message)
        }

    }


    private fun getPercentageColor(change24h: BigDecimal?): String {
        if (change24h == null) return "grey"

        return if (change24h > BigDecimal.ZERO) "#4CAF50" else "#BF360C"
    }

}
