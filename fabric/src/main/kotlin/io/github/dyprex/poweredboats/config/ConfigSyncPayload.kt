package io.github.dyprex.poweredboats.config

import io.github.dyprex.poweredboats.Constants.CONFIG_SYNC_PACKET_ID
import io.netty.buffer.ByteBuf
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload

data class ConfigSyncPayload(val config: ModConfig) : CustomPayload {

    override fun getId(): CustomPayload.Id<ConfigSyncPayload> {
        return ID
    }

    companion object {
        val ID = CustomPayload.Id<ConfigSyncPayload>(CONFIG_SYNC_PACKET_ID)
        val CODEC: PacketCodec<PacketByteBuf, ConfigSyncPayload> =
            PacketCodec.tuple(ModConfigCodec(), ConfigSyncPayload::config, ::ConfigSyncPayload)
    }
}

class ModConfigCodec : PacketCodec<ByteBuf, ModConfig> {
    override fun decode(buf: ByteBuf): ModConfig {
        return ModConfig(boatSpeed = buf.readFloat())
    }

    override fun encode(buf: ByteBuf, config: ModConfig) {
        buf.writeFloat(config.boatSpeed)
    }
}
