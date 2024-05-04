package global.common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import java.util.Locale

object TextUtil {
    const val THIN_SPACE = ' '
    const val NON_BREAKING_SPACE = ' '
    const val ELLIPSES = '…'

    val String.words: List<String>
        get() = this.replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace(Regex("[\\-_]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

    /** Example: `Sentence case` */
    val String.sentenceCase: String
        get() = words.joinToString(" ") { it.lowercase() }
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            }

    /** Example: `Title Case` */
    val String.titleCase: String
        get() = words.joinToString(" ") { word ->
            word.lowercase().replaceFirstChar {
                it.uppercase()
            }
        }

    /** Example: `snake_case` */
    val String.snakeCase: String
        get() = words.joinToString("_") {
            it.lowercase()
        }

    /** Example: `PascalCase` */
    val String.pascalCase: String
        get() = words.joinToString("") { word ->
            word.lowercase().replaceFirstChar {
                it.uppercase()
            }
        }

    /** Example: `camelCase` */
    val String.camelCase: String
        get() {
            val words = words
            if (words.isEmpty()) return ""
            val first = words[0].lowercase()
            val rest = words.drop(1)
                .joinToString("") { word ->
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
            return first + rest
        }


    val String.upper: String
        @Composable get() = this.toUpperCase(LocalLocale.current)
    val String.lower: String
        @Composable get() = this.toLowerCase(LocalLocale.current)
}