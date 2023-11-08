package com.nfeld.jsonpathkt

public data class ResolutionOptions(
  val wrapSingleValue: Boolean = false,
) {
  public companion object {
    public val Default: ResolutionOptions = ResolutionOptions()
  }
}
