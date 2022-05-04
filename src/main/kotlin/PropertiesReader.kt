import java.util.Properties

object PropertiesReader {
    private var props = Properties()
    init {
        props.load(object{}.javaClass.getResourceAsStream("general.properties"))
    }

    fun getProperty(name: String): String? {
        return props.getProperty(name)
    }
}