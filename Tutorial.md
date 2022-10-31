## Changing client name

1. Rename client name [here](src/main/java/net/ccbluex/liquidbounce/LiquidBounce.kt)
2. Rename resources directory [here](src/main/resources/assets/minecraft/liquidbounce) it should only have lower case letters

____
## Registering modules

1. Create new file in any of available directories [here](src/main/java/net/ccbluex/liquidbounce/features/module/modules)
   + Create module class
    ```kotlin
    /*
    * LiquidBounce Hacked Client
    * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
    * https://github.com/CCBlueX/LiquidBounce/
    */
    package net.ccbluex.liquidbounce.features.module.modules.CATEGORYNAME

    import net.ccbluex.liquidbounce.features.module.Module
    import net.ccbluex.liquidbounce.features.module.ModuleCategory
    import net.ccbluex.liquidbounce.features.module.ModuleInfo

    @ModuleInfo(name = "ExampleModule", description = "Example description.", category = ModuleCategory.CATEGORYNAME)
    class ExampleModule : Module()
   ```
   + Register module [in](src/main/java/net/ccbluex/liquidbounce/features/module/ModuleManager.kt)
   + Here it is

____