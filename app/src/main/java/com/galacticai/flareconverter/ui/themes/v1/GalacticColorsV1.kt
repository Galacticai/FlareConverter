package com.galacticai.flareconverter.ui.themes.v1

import androidx.compose.ui.graphics.Color

/** static colors of Galactic theme*/
object GalacticColorsV1 {
    /** dark mode */
    object Blue {
        /** dark mode (seed) */
        object Dark {
            val seed get() = blue900
            val blue50 = Color(0xFFE5E6ED)
            val blue100 = Color(0xFFBEC1D5)
            val blue200 = Color(0xFF9499B8)
            val blue300 = Color(0xFF6C739B)
            val blue400 = Color(0xFF505788)
            val blue500 = Color(0xFF343C76)
            val blue600 = Color(0xFF2F356E)
            val blue700 = Color(0xFF272D64)
            val blue800 = Color(0xFF202458)
            val blue900 = Color(0xFF141541)
            val blueDark = Color(0xFF121220)
            val blueDarker = Color(0xFF0E0E12)
        }

        /** light mode */
        object Light {
            val seed get() = blue500
            val blue50 = Color(0xFFEAECFB)
            val blue100 = Color(0xFFC9CFF4)
            val blue200 = Color(0xFFA5B0ED)
            val blue300 = Color(0xFF7F90E6)
            val blue400 = Color(0xFF6176E0)
            val blue500 = Color(0xFF435CD9)
            val blue600 = Color(0xFF3D53CE)
            val blue700 = Color(0xFF3348C1)
            val blue800 = Color(0xFF2A3EB6)
            val blue900 = Color(0xFF1A2AA2)
        }
    }

    /** light mode */
    object Yellow {
        /** light mode (seed) */
        object Light {
            val seed get() = yellow100
            val yellow50 = Color(0xFFFFF0D6)
            val yellow100 = Color(0xFFFDD99B)
            val yellow200 = Color(0xFFF7C15F)
            val yellow300 = Color(0xFFF1A81D)
            val yellow400 = Color(0xFFED9600)
            val yellow500 = Color(0xFFEA8400)
            val yellow600 = Color(0xFFE87800)
            val yellow700 = Color(0xFFE36800)
            val yellow800 = Color(0xFFDE5600)
            val yellow900 = Color(0xFFD63600)
        }

        /** dark mode */
        object Dark {
            val seed get() = yellow500
            val yellow50 = Color(0xFFFFE8BA)
            val yellow100 = Color(0xFFEAC698)
            val yellow200 = Color(0xFFCAA373)
            val yellow300 = Color(0xFFA8824C)
            val yellow400 = Color(0xFF8F682E)
            val yellow500 = Color(0xFF76500E)
            val yellow600 = Color(0xFF6C4608)
            val yellow700 = Color(0xFF5D3A00)
            val yellow800 = Color(0xFF522C00)
            val yellow900 = Color(0xFF441D00)
        }
    }
}