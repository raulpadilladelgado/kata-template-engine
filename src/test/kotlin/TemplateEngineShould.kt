import org.example.TemplateEngine
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TemplateEngineShould {
    @Test
    fun `render the same given template when there are no placeholders`() {
        val template = ""
        val variables = emptyMap<String, String>()

        val result = TemplateEngine.render(template, variables)

        assertEquals(template, result.value)
        assertContentEquals(emptyList(), result.warnings)
    }

    @Test
    fun `render the template with the placeholders replaced by their variables`() {
        val template = "Hello, \${name}! Today is \${day}. Bye, \${name}!"
        val variables = mapOf("name" to "World", "day" to "Monday")

        val result = TemplateEngine.render(template, variables)

        val expected = "Hello, World! Today is Monday. Bye, World!"
        assertEquals(expected, result.value)
        assertContentEquals(emptyList(), result.warnings)
    }

    @Test
    fun `warning when there are placeholders without variables`() {
        val template = "Hello \${name}, today is \${day}. Bye, \${other}!"
        val variables = mapOf("other" to "World")

        val result = TemplateEngine.render(template, variables)

        val expectedWarnings = listOf(
            "The variable 'name' is not defined",
            "The variable 'day' is not defined"
        )
        val expected = "Hello \${name}, today is \${day}. Bye, World!"
        assertAll(
            { assertEquals(expected, result.value) },
            { assertContentEquals(expectedWarnings, result.warnings) }
        )
    }

    @Test
    fun `ignore placeholders with no closing brackets`() {
        val template = "Hello \${name!, today is \${day}"
        val variables = mapOf("name" to "World", "day" to "Monday")

        val result = TemplateEngine.render(template, variables)

        val expected = "Hello \${name!, today is Monday"
        assertAll(
            { assertEquals(expected, result.value) },
            { assertContentEquals(emptyList(), result.warnings) }
        )
    }

    @Test
    fun `allow nested placeholders`() {
        val template = "Hello, \${\${name}}!"
        val variables = mapOf("name" to "World")

        val result = TemplateEngine.render(template, variables)

        val expected = "Hello, World!"
        assertAll(
            { assertEquals(expected, result.value) },
            { assertContentEquals(emptyList(), result.warnings) }
        )
    }
}