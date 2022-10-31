/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.features.special

object AutoReconnect {
    const val MAX: Int = 60000
    const val MIN: Int = 1000

    var isEnabled: Boolean = true
        private set
    var delay: Int = 5000
        set(value) {
            isEnabled = value < MAX

            field = value
        }
}