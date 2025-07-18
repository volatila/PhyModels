package com.volatila.phymodels.command

import com.volatila.phymodels.PhyModels
import com.volatila.phymodels.physics.Physics
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ToggleDebug : CommandExecutor{
    override fun onCommand(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>
    ): Boolean {
        PhyModels.debug = !PhyModels.debug
        return true
    }
}