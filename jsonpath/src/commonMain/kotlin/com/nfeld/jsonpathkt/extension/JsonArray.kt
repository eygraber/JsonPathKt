@file:Suppress("NOTHING_TO_INLINE")

package com.nfeld.jsonpathkt.extension

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

internal inline fun JsonArray.children(): List<JsonElement> = map { it }

internal inline fun JsonArray.getValueIfNotNullOrMissing(index: Int): JsonElement? {
  val value = getOrNull(index)
  return if (value.isNotNullOrMissing()) value else null
}
