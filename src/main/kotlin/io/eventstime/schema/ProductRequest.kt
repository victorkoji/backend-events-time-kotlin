package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductRequest(
    val name: String,
    val price: Float,

    @JsonProperty("custom_form_template")
    val customFormTemplate: String?,

    @JsonProperty("stand_id")
    val standId: Long,

    @JsonProperty("stand_category_id")
    val productCategoryId: Long
)
