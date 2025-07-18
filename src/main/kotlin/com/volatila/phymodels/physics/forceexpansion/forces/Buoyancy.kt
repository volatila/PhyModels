package com.volatila.phymodels.physics.forceexpansion.forces

import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f
import com.volatila.phymodels.debug.DebugUtil.Companion.drawParticle
import com.volatila.phymodels.physics.Physics
import com.volatila.phymodels.physics.forceexpansion.ForceAppend
import com.volatila.phymodels.physics.materialattributes.getMass
import com.volatila.phymodels.physics.obj.toJmeVector3f
import com.volatila.phymodels.physics.objmanager.ObjectManager
import com.volatila.phymodels.physics.scheduler
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.BlockDisplay

class Buoyancy: ForceAppend {
    override fun applyForce(rigidBody: PhysicsRigidBody) {
        val modelEntity = ObjectManager.livingModels.values.find { it.rigidBody == rigidBody } ?: return

        var submergedVolume = 0
        val waterMass = Material.WATER.getMass()
        val forceDirection = Vector3f(0f, 1f, 0f)
        val forceMagnitude = waterMass * -Physics.GRAVITY * 0.7f

        for ((v3i, block) in modelEntity.displayBlocks) {
            val entityLoc = block.entity.location
            val translation = block.entity.transformation.translation
            val realLoc = Location(block.entity.world,
                entityLoc.x + translation.x,
                entityLoc.y + translation.y,
                entityLoc.z + translation.z
            )

            if(!realLoc.block.isLiquid) continue
            submergedVolume++


            val forceVector = forceDirection.mult(forceMagnitude)
            val modelSize = modelEntity.model.getSize()
            val xOffset = if(modelSize.x % 2 == 0) 0.5f else 0f
            val yOffset = if(modelSize.y % 2 == 0) 0.5f else 0f
            val zOffset = if(modelSize.z % 2 == 0) 0.5f else 0f
            val localOffset = v3i.toVector3f().toJmeVector3f().clone().add(xOffset, yOffset, zOffset)
//            println("applyForceExecution")
            rigidBody.scheduler {
//                println("applyForce: $forceVector, $localOffset")
                rigidBody.applyForce(forceVector, localOffset)

            }
        }
        val totalBlocks = modelEntity.displayBlocks.size
        val inWaterProportion = submergedVolume / totalBlocks

        val appendLinearDamping = 0.69f // 浮力线性阻尼
        val appendAngularDamping = 0.59f // 浮力角阻尼

        val linearDamping = Physics.BASE_LINEAR_DAMPING + (appendLinearDamping * inWaterProportion)
        val angularDamping = Physics.BASE_ANGULAR_DAMPING + (appendAngularDamping * inWaterProportion)

        rigidBody.scheduler {
            rigidBody.linearDamping = linearDamping
            rigidBody.angularDamping = angularDamping
        }
    }
}