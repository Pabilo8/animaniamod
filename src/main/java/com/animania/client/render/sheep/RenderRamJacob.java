package com.animania.client.render.sheep;

import org.lwjgl.opengl.GL11;

import com.animania.client.models.sheep.ModelJacobSheep;
import com.animania.client.render.layer.LayerBlinking;
import com.animania.common.entities.sheep.EntityAnimaniaSheep;
import com.animania.common.entities.sheep.EntityRamJacob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRamJacob<T extends EntityRamJacob> extends RenderLiving<T>
{
	public static final Factory FACTORY = new Factory();
	private static final String modid = "animania", SheepBaseDir = "textures/entity/sheep/";

	private static final ResourceLocation[] SHEEP_TEXTURES = new ResourceLocation[] { new ResourceLocation(RenderRamJacob.modid, RenderRamJacob.SheepBaseDir + "sheep_jacob.png") };

	private static final ResourceLocation SHEEP_TEXTURE_BLINK = new ResourceLocation("animania:textures/entity/sheep/sheep_blink.png");

	private static final ResourceLocation[] SHEEP_TEXTURES_SHEARED = new ResourceLocation[] { new ResourceLocation(RenderRamJacob.modid, RenderRamJacob.SheepBaseDir + "sheep_jacob_sheared.png") };

	public RenderRamJacob(RenderManager rm)
	{
		super(rm, new ModelJacobSheep(), 0.5F);
		this.addLayer(new LayerBlinking(this, SHEEP_TEXTURE_BLINK, 0x353535));
	}

	protected void preRenderScale(EntityRamJacob entity, float f)
	{
		GL11.glScalef(0.52F, 0.52F, 0.52F);
		GL11.glTranslatef(0f, 0f, -0.5f);
		EntityAnimaniaSheep entitySheep = (EntityAnimaniaSheep) entity;
		if (entitySheep.getSleeping())
		{
			this.shadowSize = 0;
			float sleepTimer = entitySheep.getSleepTimer();
			if (sleepTimer > -0.55F)
			{
				sleepTimer = sleepTimer - 0.01F;
			}
			entity.setSleepTimer(sleepTimer);

			GlStateManager.translate(-0.25F, entity.height - 1.05F - sleepTimer, -0.25F);
			GlStateManager.rotate(6.0F, 0.0F, 0.0F, 1.0F);
		}
		else
		{
			this.shadowSize = 0.5F;
			entitySheep.setSleeping(false);
			entitySheep.setSleepTimer(0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(T entity)
	{
		if (entity.posX == -1 && entity.posY == -1 && entity.posZ == -1)
		{
			return SHEEP_TEXTURES[0];
		}

		if (!entity.getSheared())
		{
			return this.SHEEP_TEXTURES[entity.getColorNumber()];
		}
		else
		{
			return this.SHEEP_TEXTURES_SHEARED[entity.getColorNumber()];
		}
	}

	@Override
	protected void preRenderCallback(T entityliving, float f)
	{
		this.preRenderScale(entityliving, f);
	}

	static class Factory<T extends EntityRamJacob> implements IRenderFactory<T>
	{
		@Override
		public Render<? super T> createRenderFor(RenderManager manager)
		{
			return new RenderRamJacob(manager);
		}

	}
}
