package com.example.a4

class Notes {
    var noteName = ""
    var note = ""
    var id: Long? = null
    var date = ""
    var imageUri: String? = null

    override fun toString(): String {
        return """
            $noteName
            $note
            """.trimIndent()
    }
}