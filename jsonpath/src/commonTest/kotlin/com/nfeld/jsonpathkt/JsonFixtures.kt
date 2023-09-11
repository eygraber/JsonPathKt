package com.nfeld.jsonpathkt

const val SMALL_JSON = "{\"key\": 5}"
const val SMALL_JSON_ARRAY = "[1,2,3,4, $SMALL_JSON]"
const val LARGE_JSON = """[{
                                    "_id": "5c77a899cd278f94d64b996e",
                                    "index": 0,
                                    "guid": "fbb80dca-349c-4b49-8dc1-b01c6684e9b7",
                                    "isActive": true,
                                    "balance": "${'$'}1,396.91",
                                    "picture": "http://placehold.it/32x32",
                                    "age": 30,
                                    "eyeColor": "brown",
                                    "name": "Salazar Casey",
                                    "gender": "male",
                                    "company": "ACUSAGE",
                                    "email": "salazarcasey@acusage.com",
                                    "phone": "+1 (825) 518-2194",
                                    "address": "535 Bassett Avenue, Clara, Virgin Islands, 3427",
                                    "about": "Laboris ea anim exercitation occaecat incididunt magna ipsum. Nostrud aliqua culpa esse quis. Irure pariatur consectetur dolore id mollit dolor non nisi. Adipisicing incididunt ut laborum quis magna aute adipisicing proident excepteur dolore nisi incididunt officia. Velit culpa occaecat consequat ipsum consequat aliquip adipisicing.",
                                    "registered": "2018-04-06T11:49:49 +07:00",
                                    "latitude": -85.888651,
                                    "longitude": 38.287152,
                                    "tags": [
                                        "occaecat",
                                        "mollit",
                                        "ullamco",
                                        "labore",
                                        "cillum",
                                        "laboris",
                                        "qui"
                                    ],
                                    "friends": [{
                                            "id": 0,
                                            "name": "Kathrine Osborn",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 1,
                                            "name": "Vonda Howe",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 2,
                                            "name": "Harrell Pratt",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    "greeting": "Hello, Salazar Casey! You have 4 unread messages.",
                                    "favoriteFruit": "banana"
                                },
                                {
                                    "_id": "5c77a89993e1f14b74b25242",
                                    "index": 1,
                                    "guid": "a3499511-2785-4256-95ab-2a670cdd8970",
                                    "isActive": true,
                                    "balance": "${'$'}1,647.14",
                                    "picture": "http://placehold.it/32x32",
                                    "age": 20,
                                    "eyeColor": "brown",
                                    "name": "Porter Cummings",
                                    "gender": "male",
                                    "company": "ZBOO",
                                    "email": "portercummings@zboo.com",
                                    "phone": "+1 (934) 443-3056",
                                    "address": "382 Cropsey Avenue, Brownsville, Minnesota, 8135",
                                    "about": "Non laborum adipisicing laborum consequat. Labore ex aliquip adipisicing labore nisi occaecat magna sunt cillum. Occaecat tempor minim quis dolor duis tempor duis nisi culpa adipisicing est eu laborum sit. Laborum fugiat sit minim proident incididunt cillum Lorem consequat consequat cupidatat elit velit. Aliquip ad excepteur sit proident ut aute sint sit adipisicing. Nisi ex velit sit consectetur ullamco laborum esse.",
                                    "registered": "2018-03-28T10:28:58 +07:00",
                                    "latitude": 71.831798,
                                    "longitude": -6.47102,
                                    "tags": [
                                        "aliquip",
                                        "cillum",
                                        "qui",
                                        "ut",
                                        "ea",
                                        "eu",
                                        "reprehenderit"
                                    ],
                                    "friends": [{
                                            "id": 0,
                                            "name": "Mason Leach",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 1,
                                            "name": "Spencer Valenzuela",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 2,
                                            "name": "Hope Medina",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    "greeting": "Hello, Porter Cummings! You have 2 unread messages.",
                                    "favoriteFruit": "banana"
                                },
                                {
                                    "_id": "5c77a8990b5cb33ad72de49c",
                                    "index": 2,
                                    "guid": "cfd43d1f-b2ef-4253-a178-ef2005def58b",
                                    "isActive": true,
                                    "balance": "${'$'}1,931.71",
                                    "picture": "http://placehold.it/32x32",
                                    "age": 25,
                                    "eyeColor": "brown",
                                    "name": "Marie Hampton",
                                    "gender": "female",
                                    "company": "ZENCO",
                                    "email": "mariehampton@zenco.com",
                                    "phone": "+1 (991) 513-3236",
                                    "address": "447 Mayfair Drive, Waterview, South Carolina, 6589",
                                    "about": "Eu non excepteur aute ipsum occaecat et deserunt veniam minim. Ea est exercitation incididunt ut id. Nulla sit labore Lorem aliqua quis aute et excepteur reprehenderit.",
                                    "registered": "2016-06-17T03:15:38 +07:00",
                                    "latitude": 78.266157,
                                    "longitude": 123.788551,
                                    "tags": [
                                        "nulla",
                                        "elit",
                                        "ipsum",
                                        "pariatur",
                                        "ullamco",
                                        "ut",
                                        "sint"
                                    ],
                                    "friends": [{
                                            "id": 0,
                                            "name": "Felecia Bright",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "name": "Maryanne Wiggins",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 2,
                                            "name": "Marylou Caldwell",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    "greeting": "Hello, Marie Hampton! You have 2 unread messages.",
                                    "favoriteFruit": "strawberry"
                                },
                                {
                                    "_id": "5c77a899e9049ac59c961b66",
                                    "index": 3,
                                    "guid": "b19105ce-5f9b-402b-aa79-43e784204409",
                                    "isActive": true,
                                    "balance": "${'$'}1,988.61",
                                    "picture": "http://placehold.it/32x32",
                                    "age": 27,
                                    "eyeColor": "blue",
                                    "name": "Mari Pugh",
                                    "gender": "female",
                                    "company": "NITRACYR",
                                    "email": "maripugh@nitracyr.com",
                                    "phone": "+1 (919) 440-2447",
                                    "address": "785 Hamilton Walk, Osage, Arkansas, 6691",
                                    "about": "Commodo nostrud et est excepteur tempor deserunt incididunt aliquip irure eu enim pariatur dolore. Tempor officia in ullamco cupidatat tempor sunt aliqua. Ipsum est ipsum aute nisi Lorem ut velit sint.",
                                    "registered": "2015-04-18T12:18:09 +07:00",
                                    "latitude": -10.214391,
                                    "longitude": -161.704708,
                                    "tags": [
                                        "fugiat",
                                        "sit",
                                        "ad",
                                        "voluptate",
                                        "officia",
                                        "aute",
                                        "duis"
                                    ],
                                    "friends": [{
                                            "id": 0,
                                            "name": "Rios Norton",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 1,
                                            "name": "Judy Good",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 2,
                                            "name": "Rosetta Stanley",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    "greeting": "Hello, Mari Pugh! You have 7 unread messages.",
                                    "favoriteFruit": "banana"
                                },
                                {
                                    "_id": "5c77a8994c3eff3e50e09963",
                                    "index": 4,
                                    "guid": "f879acba-9cf3-4b73-ab2a-277c47fc0cbf",
                                    "isActive": true,
                                    "balance": "${'$'}3,199.50",
                                    "picture": "http://placehold.it/32x32",
                                    "age": 28,
                                    "eyeColor": "green",
                                    "name": "Margret Quinn",
                                    "gender": "female",
                                    "company": "IZZBY",
                                    "email": "margretquinn@izzby.com",
                                    "phone": "+1 (827) 486-2105",
                                    "address": "809 Crescent Street, Clarktown, Michigan, 3904",
                                    "about": "Ut do Lorem fugiat esse exercitation cillum. Id cupidatat dolore fugiat pariatur qui voluptate id anim officia sit irure aliquip. Consectetur consectetur proident enim pariatur pariatur ad do. Et elit cillum duis laboris.",
                                    "registered": "2018-05-28T07:53:36 +07:00",
                                    "latitude": 32.293366,
                                    "longitude": -138.054955,
                                    "tags": [
                                        "est",
                                        "dolor",
                                        "dolore",
                                        "exercitation",
                                        "minim",
                                        "dolor",
                                        "pariatur"
                                    ],
                                    "friends": [{
                                            "id": 0,
                                            "name": "Lora Cotton",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 1,
                                            "name": "Gaines Henry",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            "id": 2,
                                            "name": "Dorothea Irwin",
                                            "other": {
                                                "a": {
                                                    "b": {
                                                        "c": "yo"
                                                    }
                                                }
                                            }
                                        }
                                    ],
                                    "greeting": "Hello, Margret Quinn! You have 2 unread messages.",
                                    "favoriteFruit": "strawberry"
                                }, {
                                    "nums": [1, 2, 3, 4, 5]
                                }
                            ]"""

const val BOOKS_JSON = """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "price": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "price": 22.99
                        }
                    ],
                    "bicycle": {
                        "color": "red",
                        "price": 19.95
                    }
                },
                "expensive": 10
            }
        """

const val FAMILY_JSON = """
            {
                "family": {
                    "children": [{
                            "name": "Thomas",
                            "age": 13
                        },
                        {
                            "name": "Mila",
                            "age": 18
                        },
                        {
                            "name": "Konstantin",
                            "age": 29,
                            "nickname": "Kons"
                        },
                        {
                            "name": "Tracy",
                            "age": 4
                        }
                    ]
                }
            }
        """
