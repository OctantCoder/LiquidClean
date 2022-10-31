/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class Mineplex : SpeedMode("Mineplex") {

    private var speed1 = 0f
    private var speed2 = 0f
    private var wfg = false
    private var fallDistance = 0f

    override fun onUpdate() {
        val x = (mc.thePlayer ?: return).posX - (mc.thePlayer ?: return).prevPosX
        val z = (mc.thePlayer ?: return).posZ - (mc.thePlayer ?: return).prevPosZ
        val distance = kotlin.math.hypot(x, z)
        if (MovementUtils.isMoving && (mc.thePlayer ?: return).onGround) {
            (mc.thePlayer ?: return).motionY = 0.4052393
            wfg = true
            speed2 = speed1
            speed1 = 0f
        } else {
            if (wfg) {
                speed1 = (speed2 + (0.46532f * fallDistance.coerceAtMost(1f)))
                wfg = false
            } else speed1 = ((distance * 0.936f).toFloat())
            fallDistance = (mc.thePlayer ?: return).fallDistance
        }
        var minimum = 0f
        if (!wfg) minimum = 0.399900111f
        val strafe = speed1.coerceAtMost(2f).coerceAtLeast(minimum)
        MovementUtils.strafe(strafe)
    }

    override fun onMotion() {
    }

    override fun onMove(event: MoveEvent) {
    }

    override fun onDisable() {
        speed1 = 0f
        speed2 = 0f
        wfg = false
        fallDistance = 0f
    }
}
