package io.eventstime.schema

import com.fasterxml.jackson.annotation.JsonProperty

data class MenuResponse(
    val id: Long,
    val name: String,
    val products: List<ProductMenuResponse?>
)

data class ProductMenuResponse(
    val id: Long,
    val name: String,
    val price: Float,

    @JsonProperty("custom_form_template")
    val customFormTemplate: String,

    @JsonProperty("image_path")
    val imagePath: String?,

    val stand: StandResponse? = null
)
