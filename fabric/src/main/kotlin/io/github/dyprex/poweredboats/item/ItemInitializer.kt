package io.github.dyprex.poweredboats.item

import io.github.dyprex.poweredboats.Constants
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.*
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object ItemInitializer : ModInitializer {

    private val furnaceBoatItems: List<FurnaceBoatItem> =
        Constants.SUPPORTED_BOAT_TYPES.map { type ->
            val registryKey: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, FurnaceBoatItem.identifierForBoatItem(type))
            Items.register(registryKey) { FurnaceBoatItem(boatItemSupplier = { type }, key = registryKey) } as FurnaceBoatItem
        }

    override fun onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register {
            it.addAfter(
                { it -> it.item is BoatItem },
                furnaceBoatItems.map { ItemStack(it) },
                ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS,
            )
        }
    }
}
