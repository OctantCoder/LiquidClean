/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.spectre

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import kotlin.math.cos
import kotlin.math.sin

class SpectreOnGround : SpeedMode("SpectreOnGround") {
    private var speedUp = 0
    override fun onMotion() {}
    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {
        if (!MovementUtils.isMoving || (mc.thePlayer ?: return).movementInput.jump) return
        if (speedUp >= 10) {
            if ((mc.thePlayer ?: return).onGround) {
                (mc.thePlayer ?: return).motionX = 0.0
                (mc.thePlayer ?: return).motionZ = 0.0
                speedUp = 0
            }
            return
        }
        if ((mc.thePlayer ?: return).onGround && mc.gameSettings.keyBindForward.isKeyDown) {
            val f = (mc.thePlayer ?: return).rotationYaw * 0.017453292f
            (mc.thePlayer ?: return).motionX -= sin(f) * 0.145f
            (mc.thePlayer ?: return).motionZ += cos(f) * 0.145f
            event.x = (mc.thePlayer ?: return).motionX
            event.y = 0.005
            event.z = (mc.thePlayer ?: return).motionZ
            speedUp++
        }
    }
}