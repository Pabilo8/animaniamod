package com.animania.common.entities.generic.ai;

import javax.annotation.Nullable;

import com.animania.common.entities.cows.EntityBullBase;
import com.animania.common.entities.cows.EntityCowBase;
import com.animania.common.entities.interfaces.ISleeping;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GenericAIPanic<T extends EntityCreature> extends EntityAIBase
{
	private final EntityCreature entity;
	protected double speed;
	private double randPosX;
	private double randPosY;
	private double randPosZ;
	private int duration;
	private boolean hitFlag;

	public GenericAIPanic(T creature, double speedIn)
	{
		this.entity = creature;
		this.speed = speedIn;
		this.setMutexBits(1);
		this.duration = 0;
		this.hitFlag = false;
	}


	public boolean shouldExecute()
	{

		if ((this.entity instanceof EntityCowBase || this.entity instanceof EntityBullBase) && this.entity.getRevengeTarget() != null) {
			return false;
		}

		
		if(this.entity.getRevengeTarget() instanceof EntityPlayer)
		{
			if(((EntityPlayer)this.entity.getRevengeTarget()).isCreative())
				return false;
		}
		
		if (this.entity.getRevengeTarget() == null && !this.entity.isBurning())
		{
			return false;
		}
		else
		{
			if (this.entity.isBurning())
			{

				if (entity instanceof ISleeping)
				{
					if (((ISleeping)entity).getSleeping())
					{
						((ISleeping)entity).setSleeping(false);
					}
				}


				BlockPos blockpos = this.getRandPos(this.entity.world, this.entity, 20, 4);

				if (blockpos != null)
				{
					this.randPosX = (double)blockpos.getX();
					this.randPosY = (double)blockpos.getY();
					this.randPosZ = (double)blockpos.getZ();
					return true;
				}
			}

			return this.findRandomPosition();
		}
	}

	protected boolean findRandomPosition()
	{
		Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.entity, 20, 4);

		if (vec3d == null)
		{
			return false;
		}
		else
		{
			this.randPosX = vec3d.x;
			this.randPosY = vec3d.y;
			this.randPosZ = vec3d.z;
			return true;
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	 public void startExecuting()
	{
		this.entity.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	 public boolean shouldContinueExecuting()
	{
		return !this.entity.getNavigator().noPath();
	}

	 @Nullable
	 private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange)
	 {
		 BlockPos blockpos = new BlockPos(entityIn);
		 int i = blockpos.getX();
		 int j = blockpos.getY();
		 int k = blockpos.getZ();
		 float f = (float)(horizontalRange * horizontalRange * verticalRange * 2);
		 BlockPos blockpos1 = null;
		 BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		 for (int l = i - horizontalRange; l <= i + horizontalRange; ++l)
		 {
			 for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1)
			 {
				 for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1)
				 {
					 blockpos$mutableblockpos.setPos(l, i1, j1);
					 IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);

					 if (iblockstate.getMaterial() == Material.WATER)
					 {
						 float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));

						 if (f1 < f)
						 {
							 f = f1;
							 blockpos1 = new BlockPos(blockpos$mutableblockpos);
						 }
					 }
				 }
			 }
		 }

		 return blockpos1;
	 }
}