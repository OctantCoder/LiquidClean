/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import kotlin.math.pow
import kotlin.math.sqrt

class MiJump : SpeedMode("MiJump") {
    override fun onMotion() {
        if (!MovementUtils.isMoving) return
        if ((mc.thePlayer ?: return).onGround && !(mc.thePlayer ?: return).movementInput.jump) {
            (mc.thePlayer ?: return).motionY += 0.1
            val multiplier = 1.8
            (mc.thePlayer ?: return).motionX *= multiplier
            (mc.thePlayer ?: return).motionZ *= multiplier
            val currentSpeed = sqrt((mc.thePlayer ?: return).motionX.pow(2.0) + (mc.thePlayer ?: return).motionZ.pow(2.0))
            val maxSpeed = 0.66
            if (currentSpeed > maxSpeed) {
                (mc.thePlayer ?: return).motionX = (mc.thePlayer ?: return).motionX / currentSpeed * maxSpeed
                (mc.thePlayer ?: return).motionZ = (mc.thePlayer ?: return).motionZ / currentSpeed * maxSpeed
            }
        }
        MovementUtils.strafe()
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {}
}