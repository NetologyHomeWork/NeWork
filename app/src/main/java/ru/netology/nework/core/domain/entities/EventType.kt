package ru.netology.nework.core.domain.entities

enum class EventType(type: String) {
    OFFLINE("OFFLINE"),
    ONLINE("ONLINE");

    companion object {
        fun parse(type: String): EventType = EventType.values().find { it.name == type } ?: OFFLINE
    }
}