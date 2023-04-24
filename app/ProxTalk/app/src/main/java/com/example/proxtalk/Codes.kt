package com.example.proxtalk

enum class Codes(val code: Int) {
    /**
     * Response codes of activities
     */
    EXTERNAL_STORAGE_READ_CODE(100),
    LOCATION_COARSE_CODE(101),
    LOCATION_FINE_CODE(102),
    BLUETOOTH_ENABLE_CODE(98),
    LOCATION_ENABLE_CODE(99),

    NOTIFICATIONS_CHANNEL_ID(57)

}