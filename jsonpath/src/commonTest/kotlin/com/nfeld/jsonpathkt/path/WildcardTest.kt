package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class WildcardTest {
  @Test
  fun parse_should_handle_all_types_of_reads_correctly() {
    // we have obj, list, list, and scalars. It should handle them all correctly the further we go down with wildcards
    // keep in mind wildcards always return a list, and drop scalars when going up a list. Scalars dont have levels to go up, only container nodes do

    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$.*")
      .toString() shouldBe """[{"bar":[42]}]""" // in root list
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$.*.*")
      .toString() shouldBe """[[42]]""" // root list wrapped around item from next level
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[42]""" // root list wrapped around item from next level
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$.*.*.*.*")
      .toString() shouldBe "[]" // root list since wildcard always returns a list

    JsonPath.parse("""[{"bar": [42]},{"bae": [30]}]""")!!.read<JsonElement>("$.*")
      .toString() shouldBe """[{"bar":[42]},{"bae":[30]}]""" // in root list
    JsonPath.parse("""[{"bar": [42]},{"bae": [30]}]""")!!.read<JsonElement>("$.*.*")
      .toString() shouldBe """[[42],[30]]""" // root list wrapped around items from next level
    JsonPath.parse("""[{"bar": [42]},{"bae": [30]}]""")!!.read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[42,30]""" // root list wrapped around items from next level
    JsonPath.parse("""[{"bar": [42]},{"bae": [30]}]""")!!.read<JsonElement>("$.*.*.*.*")
      .toString() shouldBe "[]" // root list since wildcard always returns a list

    JsonPath.parse("""[{"bar": [42]},{"bae": [30,31]}]""")!!.read<JsonElement>("$.*")
      .toString() shouldBe """[{"bar":[42]},{"bae":[30,31]}]""" // in root list
    JsonPath.parse("""[{"bar": [42]},{"bae": [30,31]}]""")!!.read<JsonElement>("$.*.*")
      .toString() shouldBe """[[42],[30,31]]""" // root list wrapped around items from next level
    JsonPath.parse("""[{"bar": [42]},{"bae": [30,31]}]""")!!.read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[42,30,31]""" // root list wrapped around items from next level
    JsonPath.parse("""[{"bar": [42]},{"bae": [30,31]}]""")!!.read<JsonElement>("$.*.*.*.*")
      .toString() shouldBe "[]" // root list since wildcard always returns a list

    JsonPath.parse("""[{"bar": [42], "scalarkey": "scalar2"}, "scalar1"]""")!!
      .read<JsonElement>("$.*")
      .toString() shouldBe """[{"bar":[42],"scalarkey":"scalar2"},"scalar1"]""" // in root list
    JsonPath.parse("""[{"bar": [42], "scalarkey": "scalar2"}, "scalar1"]""")!!
      .read<JsonElement>("$.*.*")
      .toString() shouldBe """[[42],"scalar2"]""" // root list wrapped around items from next level
    JsonPath.parse("""[{"bar": [42], "scalarkey": "scalar2"}, "scalar1"]""")!!
      .read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[42]""" // root list wrapped around item from next level
    JsonPath.parse("""[{"bar": [42], "scalarkey": "scalar2"}, "scalar1"]""")!!
      .read<JsonElement>("$.*.*.*.*")
      .toString() shouldBe "[]" // root list since wildcard always returns a list

    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*")
      .toString() shouldBe """[[1],[2,3]]"""
    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*.*")
      .toString() shouldBe """[1,2,3]"""
    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[]"""

    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$.*")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7,[8,9,10,11]]]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$.*.*")
      .toString() shouldBe """[2,3,4,5,6,7,[8,9,10,11]]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$.*.*.*")
      .toString() shouldBe """[8,9,10,11]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$.*.*.*.*")
      .toString() shouldBe """[]"""

    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$..*")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7,[8,9,10,11]],2,3,4,5,6,7,[8,9,10,11],8,9,10,11]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$..[*]")
      .toString() shouldBe """[1,[2],[3,4],[5,6,7,[8,9,10,11]],2,3,4,5,6,7,[8,9,10,11],8,9,10,11]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$..*.*")
      .toString() shouldBe """[2,3,4,5,6,7,[8,9,10,11],8,9,10,11]"""
    JsonPath.parse("""[1,[2],[3,4],[5,6,7,[8,9,10,11]]]""")!!.read<JsonElement>("$..*..*")
      .toString() shouldBe """[2,3,4,5,6,7,[8,9,10,11],8,9,10,11,8,9,10,11]"""
  }

  @Test
  fun parse_should_handle_lists_properly() {
    // Returns list held by "bar", wrapped in the root level list since wildcard always returns list
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$[*].bar")
      .toString() shouldBe """[[42]]"""
    // Returns root level list now with the item from the inner list.
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$.*.bar.*")
      .toString() shouldBe """[42]"""
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$[*].bar[*]")
      .toString() shouldBe """[42]"""
    // This would be a wildcard on a root level list which removes the scalars on that level. It should be list, not null as wildcard always returns list
    JsonPath.parse("""[{"bar": [42]}]""")!!.read<JsonElement>("$[*].bar[*][*]")
      .toString() shouldBe """[]"""

    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*[0]")
      .toString() shouldBe """[1,2]""" // first item of each sublist in root
    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*[1]")
      .toString() shouldBe """[3]""" // second item of each sublist in root, which there is only 1 of
    JsonPath.parse("""[[1], [2,3]]""")!!.read<JsonElement>("$.*[1].*")
      .toString() shouldBe """[]""" // second item of each sublist in root, which there is only 1 of

    println(JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends").toString())
    println(JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends..name").toString())
    println(JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends..name[1]").toString())
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends.*.name")
      .toString() shouldBe """["Kathrine Osborn","Vonda Howe","Harrell Pratt","Mason Leach","Spencer Valenzuela","Hope Medina","Felecia Bright","Maryanne Wiggins","Marylou Caldwell","Rios Norton","Judy Good","Rosetta Stanley","Lora Cotton","Gaines Henry","Dorothea Irwin"]"""
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$..friends.[*].name") shouldBe listOf(
      "Kathrine Osborn",
      "Vonda Howe",
      "Harrell Pratt",
      "Mason Leach",
      "Spencer Valenzuela",
      "Hope Medina",
      "Felecia Bright",
      "Maryanne Wiggins",
      "Marylou Caldwell",
      "Rios Norton",
      "Judy Good",
      "Rosetta Stanley",
      "Lora Cotton",
      "Gaines Henry",
      "Dorothea Irwin",
    )
  }

  @Test
  fun parse_should_return_null_if_null_read_before_wildcard() {
    JsonPath.parse("{}")!!.read<JsonElement>("$.key.*") shouldBe null
    JsonPath.parse("{}")!!.read<JsonElement>("$.key[*]") shouldBe null
  }

  @Test
  fun parse_should_return_self_if_used_on_scalar() {
    JsonPath.parse("5")!!.read<Int>("$.*") shouldBe 5
    JsonPath.parse("5.34")!!.read<Double>("$.*") shouldBe 5.34
    JsonPath.parse("true")!!.read<Boolean>("$.*") shouldBe true
    JsonPath.parse("false")!!.read<Boolean>("$.*") shouldBe false
    JsonPath.parse(""""hello"""")!!.read<String>("$.*") shouldBe "hello"
  }

  @Test
  fun parse_should_get_the_values_of_the_JSON_object() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends[-1].*")
      .toString() shouldBe """[2,"Harrell Pratt",{"a":{"b":{"c":"yo"}}},2,"Hope Medina",{"a":{"b":{"c":"yo"}}},2,"Marylou Caldwell",{"a":{"b":{"c":"yo"}}},2,"Rosetta Stanley",{"a":{"b":{"c":"yo"}}},2,"Dorothea Irwin",{"a":{"b":{"c":"yo"}}}]"""
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..friends[-1][*]")
      .toString() shouldBe """[2,"Harrell Pratt",{"a":{"b":{"c":"yo"}}},2,"Hope Medina",{"a":{"b":{"c":"yo"}}},2,"Marylou Caldwell",{"a":{"b":{"c":"yo"}}},2,"Rosetta Stanley",{"a":{"b":{"c":"yo"}}},2,"Dorothea Irwin",{"a":{"b":{"c":"yo"}}}]"""
  }
}
