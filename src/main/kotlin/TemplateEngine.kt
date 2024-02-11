package org.example

class TemplateEngine {
    companion object {
        fun render(template: String, variables: Map<String, String>): RenderedWithWarnings {
            if (notContainsPlaceholders(template)) {
                return renderedWithNoWarnings(template)
            }
            val rendered = replacePlaceholdersInTemplate(variables, template)
            if (notContainsPlaceholders(rendered)) {
                return renderedWithNoWarnings(rendered)
            }
            return renderedWithMissingVariables(placeholdersFrom(rendered), rendered)
        }

        private fun notContainsPlaceholders(template: String): Boolean {
            return !(template.contains("\${") && template.contains("}"))
        }

        private fun renderedWithNoWarnings(template: String): RenderedWithWarnings {
            return RenderedWithWarnings(template, emptyList())
        }

        private fun replacePlaceholdersInTemplate(variables: Map<String, String>, template: String): String {
            return variables.entries.fold(template) { templateIterated, (variableKey, variableValue) ->
                templateIterated
                    .replace(nestedPlaceholderFormatFrom(variableKey), variableValue)
                    .replace(placeholderFormatFrom(variableKey), variableValue)
            }
        }

        private fun placeholderFormatFrom(variableKey: String) = "\${$variableKey}"

        private fun nestedPlaceholderFormatFrom(variableKey: String) = "\${\${$variableKey}}"

        private fun placeholdersFrom(template: String): List<String> {
            return template
                .split("\${")
                .filter { it.contains("}") }
                .map { it.substringBefore("}") }
                .toList()
        }

        private fun renderedWithMissingVariables(
            placeholders: List<String>,
            rendered: String
        ): RenderedWithWarnings {
            val warnings = placeholders.map { "The variable '$it' is not defined" }
            return RenderedWithWarnings(rendered, warnings)
        }
    }
}

class RenderedWithWarnings(val value: String, val warnings: List<String>)