package org.example.utils

import org.example.model.Group
import org.example.model.Product

object Mock {
    val group = listOf(
        Group(
            name = "Group A",
            description = "Description A"
        ),
        Group(
            name = "Group B",
            description = "Description B"
        ),
        Group(
            name = "Group C",
            description = "Description C"
        )
    )

    val products = listOf(
        Product(
            name = "Product 1",
            description = "Description 1",
            vendor = "Vendor 1",
            count = 12,
            price = 10.0,
            group = group[0].name
        ),
        Product(
            name = "Product 2",
            description = "Description 2",
            vendor = "Vendor 2",
            count = 1,
            price = 20.0,
            group = group[1].name
        ),
        Product(
            name = "Product 3",
            description = "Description 3",
            vendor = "Vendor 1",
            count = 159,
            price = 1056.78,
            group = group[1].name
        ),
        Product(
            name = "Product 4",
            description = "Description 4",
            vendor = "Vendor 4",
            count = 100500,
            price = 999.99,
            group = group[2].name
        )
    )
}
