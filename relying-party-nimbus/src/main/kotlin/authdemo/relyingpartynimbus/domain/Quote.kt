package authdemo.relyingpartynimbus.domain


/**
 * Serialization requires a no-args constructor which is why we need to define
 * default values for all properties.
 */
data class Quote(
        val id: Long = -1,
        val quote: String = ""
)
