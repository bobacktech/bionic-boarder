package net.bobacktech.bionicboarder.utils

/**
 * A utility class to manage predefined properties with validation.
 * This class is used in any other class that requires a fixed set of custom properties that
 * are needed in executing the logic within the class.
 *
 * @property props A mutable map of property names to their values.
 * @property pv A mutable map of property names to their corresponding validation functions.
 */
class PredefinedPropertiesUnit(
    private val props: HashMap<String, Any>,
    private val pv: HashMap<String, (Any) -> Boolean>
) {

    companion object {
        // An always true property validator. Use this when validation is not necessary.
        // Example usage: "propertyName" to VALIDATION_NOT_REQUIRED
        val VALIDATION_NOT_REQUIRED = { _: Any -> true }
    }

    /**
     *  Returns a read-only copy of the predefined properties map.
     */
    fun properties() = props.toMap()

    /**
     *  Updates a predefined property with a new value.
     */
    fun properties(property: String, value: Any) {
        when {
            props.containsKey(property)
                    && pv.containsKey(property)
                    && (pv[property]!!)(value) -> {
                props[property] = value
            }
        }
    }
}