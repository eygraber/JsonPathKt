package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.tokens.Token
import kotlin.jvm.JvmInline

@JvmInline
public value class JsonPath private constructor(
  @PublishedApi internal val tokens: List<Token>,
) {
  public inline val tokenCount: Int get() = tokens.size

  public companion object {
    public fun compile(path: String): JsonPath =
      JsonPath(
        tokens = PathCompiler.compile(path.trim()),
      )
  }
}

public inline fun <reified T> JsonPath.resolve(node: JsonNode): T? =
  tokens.fold(
    initial = node,
  ) { valueAtPath: JsonNode?, nextToken: Token ->
    valueAtPath?.let(nextToken::read)
  }?.element as? T
