package at.chaoticbits.util

import org.json.JSONObject
import java.math.BigDecimal


/**
 * Returns a BigDecimal or null from the called json object and the given key
 */
fun JSONObject.getBigDecimalOrNull(key: String): BigDecimal? {
    return if (this.isNull(key)) null else this.getBigDecimal(key)
}

/**
 * Returns a String or null from the called json object and the given key
 */
fun JSONObject.getStringOrNull(key: String): String? {
    return if (this.isNull(key)) null else this.getString(key)
}
