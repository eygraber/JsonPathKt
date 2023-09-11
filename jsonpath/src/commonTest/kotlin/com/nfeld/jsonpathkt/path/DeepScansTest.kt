package com.nfeld.jsonpathkt.path

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.LARGE_JSON
import com.nfeld.jsonpathkt.extension.read
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test

class DeepScansTest {
  @Test
  fun parse_should_get_String_list() {
    val expected = listOf(
      "Salazar Casey",
      "Kathrine Osborn",
      "Vonda Howe",
      "Harrell Pratt",
      "Porter Cummings",
      "Mason Leach",
      "Spencer Valenzuela",
      "Hope Medina",
      "Marie Hampton",
      "Felecia Bright",
      "Maryanne Wiggins",
      "Marylou Caldwell",
      "Mari Pugh",
      "Rios Norton",
      "Judy Good",
      "Rosetta Stanley",
      "Margret Quinn",
      "Lora Cotton",
      "Gaines Henry",
      "Dorothea Irwin",
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$..name") shouldBe expected
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$..name") shouldBe expected
  }

  @Test
  fun parse_should_get_Double_list() {
    JsonPath.parse(LARGE_JSON)!!.read<List<Double>>("$..latitude") shouldBe listOf(
      -85.888651,
      71.831798,
      78.266157,
      -10.214391,
      32.293366,
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<Double>>("$..['latitude']") shouldBe listOf(
      -85.888651,
      71.831798,
      78.266157,
      -10.214391,
      32.293366,
    )
  }

  @Test
  fun parse_should_get_JsonArray() {
    val expected =
      """[["occaecat","mollit","ullamco","labore","cillum","laboris","qui"],["aliquip","cillum","qui","ut","ea","eu","reprehenderit"],["nulla","elit","ipsum","pariatur","ullamco","ut","sint"],["fugiat","sit","ad","voluptate","officia","aute","duis"],["est","dolor","dolore","exercitation","minim","dolor","pariatur"]]"""
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..tags")?.toString() shouldBe expected
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$..['tags']")?.toString() shouldBe expected
  }

  @Test
  fun parse_should_get_from_longer_path() {
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[2]..name") shouldBe listOf(
      "Marie Hampton",
      "Felecia Bright",
      "Maryanne Wiggins",
      "Marylou Caldwell",
    )
    JsonPath.parse(LARGE_JSON)!!.read<List<String>>("$[2]..['name']") shouldBe listOf(
      "Marie Hampton",
      "Felecia Bright",
      "Maryanne Wiggins",
      "Marylou Caldwell",
    )
  }

  @Test
  fun parse_should_scan_to_get_the_first_item_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[0]")
      .toString() shouldBe """["nulla",{"id":0,"name":"Felecia Bright","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_the_last_item_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[-1]")
      .toString() shouldBe """["sint",{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_the_first_and_third_items_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[0,2]")
      .toString() shouldBe """["nulla","ipsum",{"id":0,"name":"Felecia Bright","other":{"a":{"b":{"c":"yo"}}}},{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_the_first_and_second_from_last_items_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[0, -2]")
      .toString() shouldBe """["nulla","ut",{"id":0,"name":"Felecia Bright","other":{"a":{"b":{"c":"yo"}}}},{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_the_first_and_second_range_items_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[0:2]")
      .toString() shouldBe """["nulla","elit",{"id":0,"name":"Felecia Bright","other":{"a":{"b":{"c":"yo"}}}},{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_the_third_and_all_following_items_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[2:]")
      .toString() shouldBe """["ipsum","pariatur","ullamco","ut","sint",{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_all_items_except_for_last_item_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[:-1]")
      .toString() shouldBe """["nulla","elit","ipsum","pariatur","ullamco","ut",{"id":0,"name":"Felecia Bright","other":{"a":{"b":{"c":"yo"}}}},{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_all_items_starting_from_second_to_last_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[-2:]")
      .toString() shouldBe """["ut","sint",{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}},{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_all_items_between_first_and_last_of_all_sublists_both_sides_exclusive() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[1:-1]")
      .toString() shouldBe """["elit","ipsum","pariatur","ullamco","ut",{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_all_items_from_second_to_last_to_4th_item_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[-2:5]")
      .toString() shouldBe """[{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}},{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_get_second_from_last_item_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[-2:-1]")
      .toString() shouldBe """["ut",{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_scan_to_keys_on_same_level_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..['name','id']")
      .toString() shouldBe """["Marie Hampton","Felecia Bright",0,"Maryanne Wiggins","Marylou Caldwell",2]"""
  }

  @Test
  fun parse_should_scan_to_keys_on_different_level_of_all_sublists() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..['name','company','id']")
      .toString() shouldBe """["Marie Hampton","ZENCO","Felecia Bright",0,"Maryanne Wiggins","Marylou Caldwell",2]"""
  }

  @Test
  fun parse_should_scan_to_get_all_items_after_the_first_of_all_sublists_even_if_end_out_of_range() {
    JsonPath.parse(LARGE_JSON)!!.read<JsonElement>("$[2]..[1:100]")
      .toString() shouldBe """["elit","ipsum","pariatur","ullamco","ut","sint",{"name":"Maryanne Wiggins","other":{"a":{"b":{"c":"yo"}}}},{"id":2,"name":"Marylou Caldwell","other":{"a":{"b":{"c":"yo"}}}}]"""
  }

  @Test
  fun parse_should_get_the_key_of_every_2nd_item_in_all_sublists() {
    val json =
      """
                {
                  "k": [{"key": "some value"}, {"key": 42}],
                  "kk": [[{"key": 100}, {"key": 200}, {"key": 300}], [{"key": 400}, {"key": 500}, {"key": 600}]],
                  "key": [0, 1]
                }
      """.trimIndent()
    JsonPath.parse(json)!!.read<JsonElement>("$..[1].key")
      .toString() shouldBe """[42,200,500]"""
  }
}
