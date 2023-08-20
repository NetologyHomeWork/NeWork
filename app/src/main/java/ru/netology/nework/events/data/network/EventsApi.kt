package ru.netology.nework.events.data.network

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nework.core.data.dto.EventDto

interface EventsApi {

    @GET("api/events/")
    suspend fun getEvents(): List<EventDto>

    @POST("api/events/{event_id}/likes/")
    suspend fun likeEvent(
        @Path("event_id") eventId: Long
    ): EventDto

    @DELETE("api/events/{event_id}/likes/")
    suspend fun dislikeEvent(
        @Path("event_id") eventId: Long
    ): EventDto

    @DELETE("api/events/{event_id}/")
    suspend fun deleteEvent(
        @Path("event_id") eventId: Long
    )

    @GET("api/events/{event_id}/")
    suspend fun getEventById(
        @Path("event_id") eventId: Long
    ): EventDto
}