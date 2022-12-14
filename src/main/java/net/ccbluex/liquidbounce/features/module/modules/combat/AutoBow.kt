/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(
    name = "AutoBow",
    description = "Automatically shoots an arrow whenever your bow is fully loaded.",
    category = ModuleCategory.COMBAT
)
class AutoBow : Module() {

    private val waitForBowAimBot = BoolValue("WaitForBowAimBot", true)

    @EventTarget
    fun onUpdate(@Suppress("UNUSED_PARAMETER") event: UpdateEvent) {
        val bowAimBot = LiquidBounce.moduleManager[BowAimBot::class.java] as BowAimBot

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isUsingItem && thePlayer.heldItem?.item is ItemBow &&
            thePlayer.itemInUseDuration > 20 && (!waitForBowAimBot.get() || !bowAimBot.state || bowAimBot.hasTarget())
        ) {
            thePlayer.stopUsingItem()
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
        }
    }
}
