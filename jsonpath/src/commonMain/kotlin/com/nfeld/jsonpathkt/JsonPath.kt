package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.extension.isNotNullOrMissing
import com.nfeld.jsonpathkt.tokens.Token
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.jvm.JvmStatic

class JsonPath(path: String) {
  private val path: String
  val tokens: List<Token>

  /**
   * Trim given path string and compile it on initialization
   */
  init {
    this.path = path.trim()

    tokens = PathCompiler.compile(this.path)
  }

  fun read(json: JsonElement): JsonElement? {
    if (json is JsonNull) return null

    return tokens.read(json)
  }

  companion object {
    /**
     * Parse JSON string and return successful [JsonNode] or null otherwise
     *
     * @param jsonString JSON string to parse
     * @return instance of parsed jackson [JsonNode] object, or null
     */
    @JvmStatic
    fun parse(jsonString: String?): JsonElement? =
      jsonString?.let {
        runCatching {
          Json
            .parseToJsonElement(it)
            .takeIf { element ->
              element.isNotNullOrMissing()
            }
        }.getOrNull()
      }
  }
}

fun JsonElement.read(path: String): JsonElement? {
  if (this is JsonNull) return JsonNull

  val trimmedPath = path.trim()

  val tokens = PathCompiler.compile(trimmedPath)

  return tokens.read(this)
}

inline fun <reified T> JsonElement.read(path: String, serializer: KSerializer<T>): T? =
  try {
    read(path)?.let { Json.decodeFromJsonElement(serializer, it) }
  } catch (_: Throwable) {
    null
  }

fun JsonElement.readString(path: String): String? =
  when (val value = read(path)) {
    is JsonPrimitive -> value.contentOrNull
    else -> null
  }

private fun List<Token>.read(json: JsonElement): JsonElement? =
  fold(initial = JsonNode(json, isNewRoot = false)) { valueAtPath: JsonNode?, nextToken: Token ->
    valueAtPath?.let(nextToken::read)
  }?.element
