package com.nfeld.jsonpathkt.json

internal class JsonArrayBuilder {
  val elements = mutableListOf<Any>()

  fun add(element: Any) {
    elements += element
  }
}
