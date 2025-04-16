// Shared code (no client dependencies)
package io.github.dyprex.poweredboats

import net.minecraft.item.Item
import net.minecraft.item.Items

enum class SupportedBoatType(val item: Item, val id: String) {
    ACACIA(Items.ACACIA_BOAT, "acacia"),
    BIRCH(Items.BIRCH_BOAT, "birch"),
    CHERRY(Items.CHERRY_BOAT, "cherry"),
    DARK_OAK(Items.DARK_OAK_BOAT, "dark_oak"),
    JUNGLE(Items.JUNGLE_BOAT, "jungle"),
    MANGROVE(Items.MANGROVE_BOAT, "mangrove"),
    OAK(Items.OAK_BOAT, "oak"),
    SPRUCE(Items.SPRUCE_BOAT, "spruce");

    companion object {
        fun fromItem(item: Item): SupportedBoatType =
            entries.firstOrNull { it.item == item } ?: OAK
    }
}
