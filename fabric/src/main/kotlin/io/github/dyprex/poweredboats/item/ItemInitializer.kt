package io.github.dyprex.poweredboats.item

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.vehicle.BoatEntity.Type.*
import net.minecraft.item.BoatItem
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object ItemInitializer : ModInitializer {

    private val supportedBoatTypes =
        listOf(ACACIA, BIRCH, CHERRY, DARK_OAK, JUNGLE, MANGROVE, OAK, SPRUCE)
    val furnaceBoatItems: List<FurnaceBoatItem> =
        supportedBoatTypes.map { type ->
            Registry.register(
                Registries.ITEM,
                FurnaceBoatItem.identifierForBoatType(type),
                FurnaceBoatItem(type),
            )
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
