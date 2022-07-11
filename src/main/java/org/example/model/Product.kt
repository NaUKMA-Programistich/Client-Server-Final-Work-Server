package org.example.model

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val vendor: String,
    val count: Int,
    val price: Double,
    val group: String
) {
    fun isValid(): String? {
        if (this.name.isEmpty()) return "Name must be not empty"
        if (this.vendor.isEmpty()) return "Vendor must be not empty"
        if (this.count < 0) return "Count must be greater than 0"
        if (this.price < 0) return "Price must be greater than 0"
        if (this.group.isEmpty()) return "Group must be not empty"
        return null
    }
}
