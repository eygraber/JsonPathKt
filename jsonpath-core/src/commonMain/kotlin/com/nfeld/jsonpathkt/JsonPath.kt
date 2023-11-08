package com.nfeld.jsonpathkt

import com.nfeld.jsonpathkt.json.JsonNode
import com.nfeld.jsonpathkt.json.JsonType
import com.nfeld.jsonpathkt.tokens.ArrayAccessorToken
import com.nfeld.jsonpathkt.tokens.ObjectAccessorToken
import com.nfeld.jsonpathkt.tokens.Token
import com.nfeld.jsonpathkt.tokens.WildcardToken
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

public inline fun <reified T> JsonPath.resolveOrNull(
  node: JsonNode,
  options: ResolutionOptions = ResolutionOptions.Default,
): T? =
  tokens.fold(
    initial = node,
  ) { valueAtPath: JsonNode?, nextToken: Token ->
    valueAtPath?.let(nextToken::read)
  }?.let {
    val isRoot = tokens.isEmpty()
    val containsWildcard = tokens.any { token -> token is WildcardToken }
    val lastToken = tokens.lastOrNull()
    val isAccessingAnObjectOrArray =
      lastToken is ObjectAccessorToken || lastToken is ArrayAccessorToken
    val isNodeAnArray = it.type == JsonType.Array

    val wrappingRequired =
      options.wrapSingleValue &&
        !containsWildcard &&
        (isRoot || isAccessingAnObjectOrArray || !isNodeAnArray)

    when {
      wrappingRequired -> it.copy(element = it.toJsonArray(listOf(it.element)))
      else -> it
    }
  }?.element as? T
