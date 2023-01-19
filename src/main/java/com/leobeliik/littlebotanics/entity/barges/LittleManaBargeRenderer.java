package com.leobeliik.littlebotanics.entity.barges;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.murad.shipping.entity.custom.vessel.VesselEntity;
import dev.murad.shipping.entity.render.barge.StaticVesselRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.RenderHelper;
import javax.annotation.ParametersAreNonnullByDefault;

public class LittleManaBargeRenderer<T extends VesselEntity> extends StaticVesselRenderer {
    public LittleManaBargeRenderer(EntityRendererProvider.Context context, ModelSupplier supplier, ModelLayerLocation location, ResourceLocation texture) {
        super(context, supplier, location, texture);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render((VesselEntity) pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        this.renderMana((LittleManaBargeEntity) pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    private void renderMana(LittleManaBargeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {

        int insideUVStart = 2;
        int insideUVEnd = 14;
        float manaLevel = (float) pEntity.getMana() / (float) pEntity.getMaxMana();

        if (manaLevel > 0) {
            pMatrixStack.pushPose();
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(pEntityYaw));
            pMatrixStack.translate(-0.5, -0.5, Mth.clampedMap(manaLevel, 0, 1, 0.6875F, 1F) * -1);

            VertexConsumer buffer = pBuffer.getBuffer(RenderHelper.MANA_POOL_WATER);
            RenderHelper.renderIconCropped(pMatrixStack, buffer, insideUVStart, insideUVStart, insideUVEnd, insideUVEnd,
                    MiscellaneousModels.INSTANCE.manaWater.sprite(), 0xFFFFFF, 1, pPackedLight);

            pMatrixStack.popPose();
        }
    }
}
