package org.example.model

data class Group(
    val id: Int = 0,
    val name: String,
    val description: String? = null
) {
    fun isValid(): String? {
        if (this.name.isEmpty()) return "Name must be not empty"
        return null
    }
}
