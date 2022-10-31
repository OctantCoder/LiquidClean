/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import kotlin.math.cos
import kotlin.math.sin

class NCPYPort : SpeedMode("NCPYPort") {
    private var jumps = 0
    override fun onMotion() {
        if ((mc.thePlayer ?: return).isOnLadder || (mc.thePlayer ?: return).isInWater || (mc.thePlayer
                ?: return).isInLava || (mc.thePlayer ?: return).isInWeb || !MovementUtils.isMoving || (mc.thePlayer
                ?: return).isInWater) return
        if (jumps >= 4 && (mc.thePlayer ?: return).onGround) jumps = 0
        if ((mc.thePlayer ?: return).onGround) {
            (mc.thePlayer ?: return).motionY = if (jumps <= 1) 0.42 else 0.4
            val f = (mc.thePlayer ?: return).rotationYaw * 0.017453292f
            (mc.thePlayer ?: return).motionX -= sin(f) * 0.2f
            (mc.thePlayer ?: return).motionZ += cos(f) * 0.2f
            jumps++
        } else if (jumps <= 1) (mc.thePlayer ?: return).motionY = -5.0
        MovementUtils.strafe()
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {}
}