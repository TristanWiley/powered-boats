package io.github.dyprex.poweredboats

import java.nio.file.Path
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier

object Constants {
    const val MOD_ID = "powered-boats"
    val CONFIG_SYNC_PACKET_ID: Identifier = Identifier.of(MOD_ID, "config_sync")
    val CONFIG_FILE_PATH: Path =
        FabricLoader.getInstance().configDir.resolve(MOD_ID).resolve("$MOD_ID.properties")
}
