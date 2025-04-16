package io.github.dyprex.poweredboats.config

import io.github.dyprex.poweredboats.Constants.CONFIG_FILE_PATH
import io.github.dyprex.poweredboats.Constants.MOD_ID
import io.github.dyprex.poweredboats.config.ModConfig.Companion.saveTo
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ConfigInitializer : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger(MOD_ID)
    lateinit var activeConfig: ModConfig

    override fun onInitialize() {
        PayloadTypeRegistry.configurationS2C()
            .register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC)

        val config = loadConfig()
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            activeConfig = config
        }
        ServerConfigurationConnectionEvents.CONFIGURE.register { handler, _ ->
            if (!ServerConfigurationNetworking.canSend(handler, ConfigSyncPayload.ID)) {
                handler.disconnect(Text.literal("Configuration of powered-boats mod failed."))
            }
            ServerConfigurationNetworking.send(handler, ConfigSyncPayload(config))
        }
    }

    private fun loadConfig(): ModConfig {
        if (!CONFIG_FILE_PATH.toFile().exists()) {
            return ModConfig().also { it.saveTo(CONFIG_FILE_PATH) }
        }
        return ModConfig.loadFrom(CONFIG_FILE_PATH).getOrElse {
            logger.error("""Failed to load config file "$CONFIG_FILE_PATH".""", it)
            ModConfig()
        }
    }
}
