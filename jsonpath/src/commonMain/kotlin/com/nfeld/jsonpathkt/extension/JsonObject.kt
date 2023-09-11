@file:Suppress("NOTHING_TO_INLINE")

package com.nfeld.jsonpathkt.extension

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal inline fun JsonObject.getValueIfNotNullOrMissing(key: String): JsonElement? {
  val value = get(key)
  return if (value.isNotNullOrMissing()) value else null
}
