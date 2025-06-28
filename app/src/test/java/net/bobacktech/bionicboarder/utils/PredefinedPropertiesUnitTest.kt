package net.bobacktech.bionicboarder.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PredefinedPropertiesUnitTest {

    private lateinit var props: HashMap<String, Any>
    private lateinit var pv: HashMap<String, (Any) -> Boolean>
    private lateinit var predefinedPropertiesUnit: PredefinedPropertiesUnit

    @BeforeEach
    fun setUp() {
        // Initialize the properties and validators
        props = hashMapOf(
            "property1" to 10,
            "property2" to "default"
        )

        pv = hashMapOf(
            "property1" to { value -> value is Int && value > 0 },
            "property2" to { value -> value is String && value.isNotEmpty() }
        )

        // Create an instance of PredefinedPropertiesUnit
        predefinedPropertiesUnit = PredefinedPropertiesUnit(props, pv)
    }

    @Test
    fun `test properties() returns a read-only copy of the properties`() {
        val properties = predefinedPropertiesUnit.properties()

        // Verify the properties match the initial values
        assertEquals(10, properties["property1"])
        assertEquals("default", properties["property2"])

        // Ensure the returned map is read-only
        assertThrows(UnsupportedOperationException::class.java) {
            (properties as MutableMap)["property1"] = 20
        }
    }

    @Test
    fun `test properties(property, value) updates a valid property`() {
        // Update property1 with a valid value
        predefinedPropertiesUnit.properties("property1", 20)

        // Verify the property was updated
        val updatedProperties = predefinedPropertiesUnit.properties()
        assertEquals(20, updatedProperties["property1"])
    }

    @Test
    fun `test properties(property, value) does not update an invalid property`() {
        // Attempt to update property1 with an invalid value
        predefinedPropertiesUnit.properties("property1", -5)

        // Verify the property was not updated
        val updatedProperties = predefinedPropertiesUnit.properties()
        assertEquals(10, updatedProperties["property1"])
    }

    @Test
    fun `test properties(property, value) does not update a non-existent property`() {
        // Attempt to update a non-existent property
        predefinedPropertiesUnit.properties("nonExistentProperty", "value")

        // Verify the non-existent property was not added
        val updatedProperties = predefinedPropertiesUnit.properties()
        assertFalse(updatedProperties.containsKey("nonExistentProperty"))
    }

    @Test
    fun `test VALIDATION_NOT_REQUIRED allows any value`() {
        // Add a property with VALIDATION_NOT_REQUIRED
        props["property3"] = "initial"
        pv["property3"] = PredefinedPropertiesUnit.VALIDATION_NOT_REQUIRED

        // Update the property with any value
        predefinedPropertiesUnit.properties("property3", 12345)

        // Verify the property was updated
        val updatedProperties = predefinedPropertiesUnit.properties()
        assertEquals(12345, updatedProperties["property3"])
    }
}