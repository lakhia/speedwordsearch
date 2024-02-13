package creationsahead.com.speedwordsearch

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import creationsahead.speedwordsearch.ui.GridWidget
import creationsahead.speedwordsearch.ui.WordListWidget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("creationsahead.com.speedwordsearch", appContext.packageName)
    }

    @Test
    fun gridWidget() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val widget = GridWidget(appContext, null)
        assertNotNull(widget)
    }

    @Test
    fun wordListWidget() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val widget = WordListWidget(appContext, null)
        assertNotNull(widget)
    }
}
