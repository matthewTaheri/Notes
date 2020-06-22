package com.example.a4

class DataClass {
    var noteName = ""
    var note = ""
    var id: Long? = null
    var date = ""

    override fun toString(): String {
        return """
            $noteName
            $note
            """.trimIndent()
    }
}