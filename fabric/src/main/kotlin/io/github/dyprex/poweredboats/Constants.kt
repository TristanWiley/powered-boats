package io.github.dyprex.poweredboats

import java.nio.file.Path
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Items
import net.minecraft.util.Identifier

object Constants {
    const val MOD_ID = "powered-boats"
    val CONFIG_SYNC_PACKET_ID: Identifier = Identifier.of(MOD_ID, "config_sync")
    val CONFIG_FILE_PATH: Path =
        FabricLoader.getInstance().configDir.resolve(MOD_ID).resolve("$MOD_ID.properties")

    val SUPPORTED_BOAT_TYPES =
        listOf(Items.ACACIA_BOAT, Items.BIRCH_BOAT, Items.CHERRY_BOAT, Items.DARK_OAK_BOAT, Items.JUNGLE_BOAT, Items.MANGROVE_BOAT, Items.OAK_BOAT, Items.SPRUCE_BOAT)
}
