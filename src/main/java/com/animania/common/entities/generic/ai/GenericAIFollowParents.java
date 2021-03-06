package com.animania.common.entities.generic.ai;

import java.util.List;

import com.animania.common.entities.interfaces.IChild;
import com.animania.common.entities.interfaces.IMateable;
import com.animania.common.entities.interfaces.ISleeping;
import com.animania.common.entities.pigs.EntityAnimaniaPig;
import com.animania.common.entities.pigs.EntityPigletBase;
import com.animania.common.entities.pigs.EntitySowBase;
import com.animania.common.helper.AnimaniaHelper;
import com.animania.config.AnimaniaConfig;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class GenericAIFollowParents<T extends EntityCreature & IChild & ISleeping, O extends EntityCreature & IMateable> extends EntityAIBase
{
	T childAnimal;
	O parentAnimal;
	double moveSpeed;
	private int delayCounter;
	Class mother;

	public GenericAIFollowParents(T animal, double speed, Class mother)
	{
		this.childAnimal = animal;
		this.moveSpeed = speed;
		this.mother = mother;
	}

	@Override
	public boolean shouldExecute()
	{

		this.delayCounter++;
		if (this.delayCounter > AnimaniaConfig.gameRules.ticksBetweenAIFirings)
		{

			if (!childAnimal.world.isDaytime() || childAnimal.getSleeping())
			{
				this.delayCounter = 0;
				return false;
			}

			if (childAnimal.getParentUniqueId() == null)
			{
				return false;
			}
			else
			{
				List entities = AnimaniaHelper.getEntitiesInRange(mother, 40, this.childAnimal.world, this.childAnimal);

				for (int k = 0; k <= entities.size() - 1; k++)
				{

					O mother = (O) entities.get(k);

					if (mother != null && mother.getPersistentID().equals(this.childAnimal.getParentUniqueId()))
					{

						double xt = mother.posX;
						double yt = mother.posY;
						double zt = mother.posZ;
						int x1 = MathHelper.floor(this.childAnimal.posX);
						int y1 = MathHelper.floor(this.childAnimal.posY);
						int z1 = MathHelper.floor(this.childAnimal.posZ);
						double x2 = Math.abs(xt - x1);
						double y2 = Math.abs(yt - y1);
						double z2 = Math.abs(zt - z1);

						if (x2 <= 20 && y2 <= 8 && z2 <= 20 && x2 >= 3 && z2 >= 3)
						{
							this.parentAnimal =  mother;
							return true;
						}
						else
						{
							return false;
						}
					}
				}
			}

		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (!this.parentAnimal.isEntityAlive())
			return false;
		else
		{
			double d0 = this.childAnimal.getDistanceSq(this.parentAnimal);
			return d0 >= 9.0D && d0 <= 256.0D;
		}
	}

	@Override
	public void startExecuting()
	{
		this.delayCounter = 0;
	}

	@Override
	public void resetTask()
	{
		this.parentAnimal = null;
	}

	@Override
	public void updateTask()
	{
		if (--this.delayCounter <= 0)
		{
			this.delayCounter = 40;
			this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
		}
	}
}
