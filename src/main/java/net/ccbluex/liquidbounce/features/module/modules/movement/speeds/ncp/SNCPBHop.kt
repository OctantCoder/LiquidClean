/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.potion.Potion
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SNCPBHop : SpeedMode("SNCPBHop") {
    private var level = 1
    private var moveSpeed = 0.2873
    private var lastDist = 0.0
    private var timerDelay = 0
    override fun onEnable() {
        mc.timer.timerSpeed = 1f
        lastDist = 0.0
        moveSpeed = 0.0
        level = 4
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        moveSpeed = baseMoveSpeed
        level = 0
    }

    override fun onMotion() {
        val xDist = (mc.thePlayer ?: return).posX - (mc.thePlayer ?: return).prevPosX
        val zDist = (mc.thePlayer ?: return).posZ - (mc.thePlayer ?: return).prevPosZ
        lastDist = sqrt(xDist * xDist + zDist * zDist)
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {
        ++timerDelay
        timerDelay %= 5
        if (timerDelay != 0) {
            mc.timer.timerSpeed = 1f
        } else {
            if (MovementUtils.isMoving) mc.timer.timerSpeed = 32767f
            if (MovementUtils.isMoving) {
                mc.timer.timerSpeed = 1.3f
                (mc.thePlayer ?: return).motionX *= 1.0199999809265137
                (mc.thePlayer ?: return).motionZ *= 1.0199999809265137
            }
        }
        if ((mc.thePlayer ?: return).onGround && MovementUtils.isMoving) level = 2
        if (round((mc.thePlayer ?: return).posY - (mc.thePlayer ?: return).posY.toInt().toDouble()) == round(0.138)) {
            (mc.thePlayer ?: return).motionY -= 0.08
            event.y = event.y - 0.09316090325960147
            (mc.thePlayer ?: return).posY -= 0.09316090325960147
        }
        if (level == 1 && ((mc.thePlayer ?: return).moveForward != 0.0f || (mc.thePlayer ?: return).moveStrafing != 0.0f)) {
            level = 2
            moveSpeed = 1.35 * baseMoveSpeed - 0.01
        } else if (level == 2) {
            level = 3
            (mc.thePlayer ?: return).motionY = 0.399399995803833
            event.y = 0.399399995803833
            moveSpeed *= 2.149
        } else if (level == 3) {
            level = 4
            val difference = 0.66 * (lastDist - baseMoveSpeed)
            moveSpeed = lastDist - difference
        } else if (level == 88) {
            moveSpeed = baseMoveSpeed
            lastDist = 0.0
            level = 89
        } else if (level == 89) {
            if ((mc.theWorld ?: return).getCollidingBoundingBoxes(
                    mc.thePlayer ?: return,
                    (mc.thePlayer ?: return).entityBoundingBox.offset(0.0, (mc.thePlayer ?: return).motionY, 0.0)
                ).isNotEmpty() || (mc.thePlayer ?: return).isCollidedVertically
            ) level = 1
            lastDist = 0.0
            moveSpeed = baseMoveSpeed
            return
        } else {
            if ((mc.theWorld ?: return).getCollidingBoundingBoxes(
                    mc.thePlayer ?: return,
                    (mc.thePlayer ?: return).entityBoundingBox.offset(0.0, (mc.thePlayer ?: return).motionY, 0.0)
                ).isNotEmpty() || (mc.thePlayer ?: return).isCollidedVertically
            ) {
                moveSpeed = baseMoveSpeed
                lastDist = 0.0
                level = 88
                return
            }
            moveSpeed = lastDist - lastDist / 159.0
        }
        moveSpeed = moveSpeed.coerceAtLeast(baseMoveSpeed)

        val movementInput = (mc.thePlayer ?: return).movementInput
        var forward: Float = movementInput.moveForward
        var strafe: Float = movementInput.moveStrafe
        var yaw = (mc.thePlayer ?: return).rotationYaw
        if (forward == 0.0f && strafe == 0.0f) {
            event.x = 0.0
            event.z = 0.0
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += (if (forward > 0.0f) -45 else 45).toFloat()
                strafe = 0.0f
            } else if (strafe <= -1.0f) {
                yaw += (if (forward > 0.0f) 45 else -45).toFloat()
                strafe = 0.0f
            }
            if (forward > 0.0f) {
                forward = 1.0f
            } else if (forward < 0.0f) {
                forward = -1.0f
            }
        }
        val mx2 = cos(Math.toRadians(yaw + 90.0f.toDouble()))
        val mz2 = sin(Math.toRadians(yaw + 90.0f.toDouble()))
        event.x = forward.toDouble() * moveSpeed * mx2 + strafe.toDouble() * moveSpeed * mz2
        event.z = forward.toDouble() * moveSpeed * mz2 - strafe.toDouble() * moveSpeed * mx2
        (mc.thePlayer ?: return).stepHeight = 0.6f
        if (forward == 0.0f && strafe == 0.0f) {
            event.x = 0.0
            event.z = 0.0
        }
    }

    private val baseMoveSpeed: Double
        get() {
            var baseSpeed = 0.2873
            if (mc.thePlayer!!.isPotionActive(Potion.moveSpeed)) baseSpeed *= 1.0 + 0.2 * (mc.thePlayer!!.getActivePotionEffect(
                Potion.moveSpeed
            )!!.amplifier + 1)
            return baseSpeed
        }

    private fun round(value: Double): Double {
        var bigDecimal = BigDecimal(value)
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP)
        return bigDecimal.toDouble()
    }
}