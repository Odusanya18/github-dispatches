package com.spinnakersummit.orca.models

import com.fasterxml.jackson.annotation.JsonProperty

data class DispatchEvent(
    @JsonProperty("event_type")
    val eventType : String,
    @JsonProperty("client_payload")
    val clientPayload: String
)