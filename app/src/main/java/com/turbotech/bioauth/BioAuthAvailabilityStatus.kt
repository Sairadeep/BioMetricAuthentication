package com.turbotech.bioauth

enum class BioAuthAvailabilityStatus(val statusCode: Int) {
    READY(1),
    NOT_AVAILABLE(0),
    TEMPORARILY_NOT_AVAILABLE(-1),
    AVAILABLE_BUT_NOT_SET(99)
}