package ru.cscenter.fingerpaint

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.cscenter.fingerpaint.resources.*
import ru.cscenter.fingerpaint.test.R

@RunWith(Parameterized::class)
class InvalidGameConfigTest(
    private val id: Int
) {
    private val context = InstrumentationRegistry.getInstrumentation().context

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Any> = listOf(
            R.xml.test_no_colors_game_config,
            R.xml.test_no_figures_game_config,
            R.xml.test_no_letters_game_config,

            R.xml.test_no_color_id_game_config,
            R.xml.test_no_figure_id_game_config,
            R.xml.test_no_letter_id_game_config,

            R.xml.test_no_color_name_game_config,
            R.xml.test_no_figure_name_game_config,
            R.xml.test_no_letter_name_game_config,

            R.xml.test_double_color_id_game_config,
            R.xml.test_double_figure_id_game_config,
            R.xml.test_double_letter_id_game_config,

            R.xml.test_double_color_name_game_config,
            R.xml.test_double_figure_name_game_config,
            R.xml.test_double_letter_name_game_config,

            R.xml.test_illegal_color_tag_game_config,
            R.xml.test_illegal_figure_tag_game_config,
            R.xml.test_illegal_letter_tag_game_config,

            R.xml.test_illegal_colors_tag_game_config,
            R.xml.test_illegal_figures_tag_game_config,
            R.xml.test_illegal_letters_tag_game_config,

            R.xml.test_invalid_color_game_config,
            R.xml.test_invalid_figure_game_config,
            R.xml.test_invalid_letter_game_config
        )
    }

    @Test(expected = IllegalStateException::class)
    fun testFails() {
        GameResourceParser(context, id).parseXML()
    }
}

@RunWith(AndroidJUnit4::class)
class GameConfigTest {
    private val context = InstrumentationRegistry.getInstrumentation().context

    private fun checkValid(resources: GameResources) {
        assertEquals(
            listOf(
                MyColor(Color.parseColor("#1565C0"), "синюю"),
                MyColor(Color.parseColor("#FFEA00"), "жёлтую"),
                MyColor(Color.parseColor("#F44336"), "красную"),
                MyColor(Color.parseColor("#43A047"), "зеленую"),
                MyColor(Color.parseColor("#FF6D00"), "оранжевую")
            ), resources.colors
        )

        assertEquals(
            listOf(
                Figure(R.drawable.test_oval, "овал"),
                Figure(R.drawable.test_square, "квадрат")
            ),
            resources.figures
        )

        assertEquals(
            listOf(
                Letter(R.drawable.test_a, "А"),
                Letter(R.drawable.test_b, "Б")
            ),
            resources.letters
        )
    }

    @Test
    fun testValidGameConfig() {
        val resources = GameResourceParser(context, R.xml.test_valid_game_config).parseXML()
        checkValid(resources)
    }

    @Test
    fun testMixedValidGameConfig() {
        val resources = GameResourceParser(context, R.xml.test_mixed_valid_game_config).parseXML()
        checkValid(resources)
    }
}
