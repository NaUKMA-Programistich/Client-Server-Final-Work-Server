package org.example.model

data class CountProduct(
    val name: String,
    val count: Int
) {
    fun isValid(): String? {
        if (count < 0) return "Count must be greater than 0"
        if (this.name.isEmpty()) return "Name must be not empty"
        return null
    }
}
