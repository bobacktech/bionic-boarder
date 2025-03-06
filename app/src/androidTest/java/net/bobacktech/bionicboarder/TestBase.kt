package net.bobacktech.bionicboarder

import org.junit.jupiter.api.TestInstance
import androidx.test.platform.app.InstrumentationRegistry
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class TestBase {

    val properties: Properties

    init {
        val testContext = InstrumentationRegistry.getInstrumentation().context
        properties = Properties()
        val assetManager = testContext.assets
        val inputStream = assetManager.open("test.properties")
        properties.load(inputStream)
        inputStream.close()
    }
}