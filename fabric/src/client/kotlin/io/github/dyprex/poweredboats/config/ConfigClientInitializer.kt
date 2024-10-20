package io.github.dyprex.poweredboats.config

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking

object ConfigClientInitializer : ClientModInitializer {

    override fun onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(ConfigSyncPayload.ID) {
            payload: ConfigSyncPayload,
            _ ->
            ConfigInitializer.activeConfig = payload.config
        }
    }
}
