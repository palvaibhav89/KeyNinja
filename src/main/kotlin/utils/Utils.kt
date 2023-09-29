package utils

import com.google.gson.Gson
import com.google.gson.JsonObject


fun String?.jsonToJsonObject(): JsonObject? =
    Gson().fromJson(this, JsonObject::class.java)