package com.nfeld.jsonpathkt.json

public enum class JsonType {
  Array,
  Object,
  Null,
  Primitive,
  ;

  public inline val isArrayOrObject: Boolean get() = this == Array || this == Object
}
