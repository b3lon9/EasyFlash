package com.b3lon9.easyflash.constant

class Constant {
    object Level {
        const val FLASH_LEVEL1 = 1
        const val FLASH_LEVEL2 = 2
        const val FLASH_LEVEL3 = 3
        const val FLASH_LEVEL4 = 4
        const val FLASH_LEVEL5 = 5
        const val FLASH_MIN = FLASH_LEVEL1
        const val FLASH_MAX = FLASH_LEVEL5
    }

    enum class Direct {
        NORMAL,
        UP,
        DOWN,
    }

    enum class Theme {
        GREEN,
        NAVY,
        PINK,
    }
}