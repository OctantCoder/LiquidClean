/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.VecRotation
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemBucket
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import java.util.*
import kotlin.math.ceil
import kotlin.math.sqrt

@ModuleInfo(name = "NoFall", description = "Prevents you from taking fall damage.", category = ModuleCategory.PLAYER)
class NoFall : Module() {
    @JvmField
    val modeValue: ListValue = ListValue(
        "Mode", arrayOf(
            "SpoofGround",
            "NoGround",
            "Packet",
            "MLG",
            "AAC",
            "LAAC",
            "AAC3.3.11",
            "AAC3.3.15",
            "Spartan",
            "CubeCraft",
            "Hypixel"
        ), "SpoofGround"
    )
    private val minFallDistance = FloatValue("MinMLGHeight", 5f, 2f, 50f)
    private val spartanTimer = TickTimer()
    private val mlgTimer = TickTimer()
    private var currentState = 0
    private var jumped = false
    private var currentMlgRotation: VecRotation? = null
    private var currentMlgItemIndex = 0
    private var currentMlgBlock: BlockPos? = null

    @EventTarget(ignoreCondition = true)
    fun onUpdate(@Suppress("UNUSED_PARAMETER") event: UpdateEvent?) {
        if ((mc.thePlayer ?: return).onGround) jumped = false

        if ((mc.thePlayer ?: return).motionY > 0) jumped = true

        if (!state || LiquidBounce.moduleManager.getModule(FreeCam::class.java).state) return

        if (collideBlock((mc.thePlayer ?: return).entityBoundingBox) { it is BlockLiquid } || collideBlock(
                AxisAlignedBB.fromBounds(
                    (mc.thePlayer ?: return).entityBoundingBox.maxX,
                    (mc.thePlayer ?: return).entityBoundingBox.maxY,
                    (mc.thePlayer ?: return).entityBoundingBox.maxZ,
                    (mc.thePlayer ?: return).entityBoundingBox.minX,
                    (mc.thePlayer ?: return).entityBoundingBox.minY - 0.01,
                    (mc.thePlayer ?: return).entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "packet" -> {
                if ((mc.thePlayer ?: return).fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
            }

            "cubecraft" -> if ((mc.thePlayer ?: return).fallDistance > 2f) {
                (mc.thePlayer ?: return).onGround = false
                (mc.thePlayer ?: return).sendQueue.addToSendQueue(C03PacketPlayer(true))
            }

            "aac" -> {
                if ((mc.thePlayer ?: return).fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                    currentState = 2
                } else if (currentState == 2 && (mc.thePlayer ?: return).fallDistance < 2) {
                    (mc.thePlayer ?: return).motionY = 0.1
                    currentState = 3
                    return
                }
                when (currentState) {
                    3 -> {
                        (mc.thePlayer ?: return).motionY = 0.1
                        currentState = 4
                    }

                    4 -> {
                        (mc.thePlayer ?: return).motionY = 0.1
                        currentState = 5
                    }

                    5 -> {
                        (mc.thePlayer ?: return).motionY = 0.1
                        currentState = 1
                    }
                }
            }

            "laac" -> if (!jumped && (mc.thePlayer ?: return).onGround && !(mc.thePlayer ?: return).isOnLadder && !(mc.thePlayer
                    ?: return).isInWater && !(mc.thePlayer ?: return).isInWeb) (mc.thePlayer ?: return).motionY =
                (-6).toDouble()

            "aac3.3.11" -> if ((mc.thePlayer ?: return).fallDistance > 2) {
                (mc.thePlayer ?: return).motionZ = 0.0
                (mc.thePlayer ?: return).motionX = (mc.thePlayer ?: return).motionZ
                mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        (mc.thePlayer ?: return).posX, (mc.thePlayer ?: return).posY - 10E-4, (mc.thePlayer ?: return).posZ, (mc.thePlayer
                            ?: return).onGround
                    )
                )
                mc.netHandler.addToSendQueue(C03PacketPlayer(true))
            }

            "aac3.3.15" -> if ((mc.thePlayer ?: return).fallDistance > 2) {
                if (!mc.isIntegratedServerRunning) mc.netHandler.addToSendQueue(
                    C03PacketPlayer.C04PacketPlayerPosition(
                        (mc.thePlayer ?: return).posX, Double.NaN, (mc.thePlayer ?: return).posZ, false
                    )
                )
                (mc.thePlayer ?: return).fallDistance = (-9999).toFloat()
            }

            "spartan" -> {
                spartanTimer.update()
                if ((mc.thePlayer ?: return).fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            (mc.thePlayer ?: return).posX, (mc.thePlayer ?: return).posY + 10, (mc.thePlayer ?: return).posZ, true
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C03PacketPlayer.C04PacketPlayerPosition(
                            (mc.thePlayer ?: return).posX, (mc.thePlayer ?: return).posY - 10, (mc.thePlayer ?: return).posZ, true
                        )
                    )
                    spartanTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val mode = modeValue.get()
        if (packet is C03PacketPlayer) {
            if (mode.equals("SpoofGround", ignoreCase = true)) packet.onGround = true
            if (mode.equals("NoGround", ignoreCase = true)) packet.onGround = false
            if (mode.equals(
                    "Hypixel", ignoreCase = true
                ) && mc.thePlayer != null && (mc.thePlayer ?: return).fallDistance > 1.5
            ) packet.onGround = (mc.thePlayer ?: return).ticksExisted % 2 == 0
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (collideBlock(
                (mc.thePlayer ?: return).entityBoundingBox
            ) { it is BlockLiquid } || collideBlock(
                AxisAlignedBB.fromBounds(
                    (mc.thePlayer ?: return).entityBoundingBox.maxX,
                    (mc.thePlayer ?: return).entityBoundingBox.maxY,
                    (mc.thePlayer ?: return).entityBoundingBox.maxZ,
                    (mc.thePlayer ?: return).entityBoundingBox.minX,
                    (mc.thePlayer ?: return).entityBoundingBox.minY - 0.01,
                    (mc.thePlayer ?: return).entityBoundingBox.minZ
                )
            ) { it is BlockLiquid }
        ) return

        if (modeValue.get().equals("laac", ignoreCase = true)) {
            if (!jumped && !(mc.thePlayer ?: return).onGround && !(mc.thePlayer ?: return).isOnLadder && !(mc.thePlayer
                    ?: return).isInWater && !(mc.thePlayer ?: return).isInWeb && (mc.thePlayer ?: return).motionY < 0.0) {
                event.x = 0.0
                event.z = 0.0
            }
        }
    }

    @EventTarget
    private fun onMotionUpdate(event: MotionEvent) {
        if (!modeValue.get().equals("MLG", ignoreCase = true)) return

        if (event.eventState == EventState.PRE) {
            currentMlgRotation = null

            mlgTimer.update()

            if (!mlgTimer.hasTimePassed(10)) return

            if ((mc.thePlayer ?: return).fallDistance > minFallDistance.get()) {
                val fallingPlayer = FallingPlayer(
                    (mc.thePlayer ?: return).posX,
                    (mc.thePlayer ?: return).posY,
                    (mc.thePlayer ?: return).posZ,
                    (mc.thePlayer ?: return).motionX,
                    (mc.thePlayer ?: return).motionY,
                    (mc.thePlayer ?: return).motionZ,
                    (mc.thePlayer ?: return).rotationYaw,
                    (mc.thePlayer ?: return).moveStrafing,
                    (mc.thePlayer ?: return).moveForward
                )

                val maxDist: Double = mc.playerController.blockReachDistance + 1.5

                val collision =
                    fallingPlayer.findCollision(ceil(1.0 / (mc.thePlayer ?: return).motionY * -maxDist).toInt()) ?: return

                var ok: Boolean = Vec3(
                    (mc.thePlayer ?: return).posX, (mc.thePlayer ?: return).posY + (mc.thePlayer
                        ?: return).eyeHeight, (mc.thePlayer ?: return).posZ
                ).distanceTo(
                    Vec3(collision.pos).addVector(
                        0.5, 0.5, 0.5
                    )
                ) < mc.playerController.blockReachDistance + sqrt(0.75)

                if ((mc.thePlayer ?: return).motionY < collision.pos.y + 1 - (mc.thePlayer ?: return).posY) {
                    ok = true
                }

                if (!ok) return

                var index = -1

                for (i in 36..44) {
                    val itemStack = (mc.thePlayer ?: return).inventoryContainer.getSlot(i).stack

                    if (itemStack != null && (itemStack.item == Items.water_bucket || itemStack.item is ItemBlock && (itemStack.item as ItemBlock).block == Blocks.web)
                    ) {
                        index = i - 36

                        if ((mc.thePlayer ?: return).inventory.currentItem == index) break
                    }
                }
                if (index == -1) return

                currentMlgItemIndex = index
                currentMlgBlock = collision.pos

                if ((mc.thePlayer ?: return).inventory.currentItem != index) {
                    (mc.thePlayer ?: return).sendQueue.addToSendQueue(C09PacketHeldItemChange(index))
                }

                currentMlgRotation = RotationUtils.faceBlock(collision.pos)
                (currentMlgRotation ?: return).rotation.toPlayer(mc.thePlayer ?: return)
            }
        } else if (currentMlgRotation != null) {
            val stack = (mc.thePlayer ?: return).inventory.getStackInSlot(currentMlgItemIndex)

            if ((stack ?: return).item is ItemBucket) {
                mc.playerController.sendUseItem(mc.thePlayer ?: return, mc.theWorld ?: return, stack)
            } else {
                if (mc.playerController.sendUseItem(mc.thePlayer ?: return, mc.theWorld ?: return, stack)) {
                    mlgTimer.reset()
                }
            }
            if ((mc.thePlayer ?: return).inventory.currentItem != currentMlgItemIndex) (mc.thePlayer ?: return).sendQueue.addToSendQueue(
                C09PacketHeldItemChange((mc.thePlayer ?: return).inventory.currentItem)
            )
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(@Suppress("UNUSED_PARAMETER") event: JumpEvent?) {
        jumped = true
    }

    override val tag: String
        get() = modeValue.get()
}