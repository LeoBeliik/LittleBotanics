package com.leobeliik.littlebotanics.entity.cars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity;
import dev.murad.shipping.entity.models.ChainModel;
import dev.murad.shipping.entity.render.RenderWithAttachmentPoints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;
import static com.leobeliik.littlebotanics.LittleBotanics.MODID;

public class LittleManaCarRenderer<T extends AbstractTrainCarEntity> extends EntityRenderer<T> implements RenderWithAttachmentPoints<T> {
    private final EntityModel entityModel;
    private final ResourceLocation texture;
    private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation("littlelogistics", "textures/entity/chain.png");
    private final ChainModel chainModel;
    private static final ManaPoolBlockEntity DUMMY = new ManaPoolBlockEntity(ManaBurst.NO_SOURCE, BotaniaBlocks.manaPool.defaultBlockState());

    public LittleManaCarRenderer(EntityRendererProvider.Context context, Function<ModelPart, EntityModel> baseModel, ModelLayerLocation layerLocation, String baseTexture) {
        super(context);
        this.chainModel = new ChainModel(context.bakeLayer(ChainModel.LAYER_LOCATION));
        this.entityModel = baseModel.apply(context.bakeLayer(layerLocation));
        this.texture = new ResourceLocation(MODID, baseTexture);
    }

    @ParametersAreNonnullByDefault
    public void render(T car, float yaw, float pPartialTicks, PoseStack pose, MultiBufferSource buffer, int pPackedLight) {
        if (car.getDominant().isEmpty()) {
            pose.pushPose();
            AbstractTrainCarEntity t = car;

            AbstractTrainCarEntity nextT;
            for (Pair<Vec3, Vec3> attachmentPoints = this.renderCarAndGetAttachmentPoints(car, yaw, pPartialTicks, pose, buffer, pPackedLight); t.getDominated().isPresent(); t = nextT) {
                nextT = t.getDominated().get();
                EntityRenderer<? super AbstractTrainCarEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(nextT);
                if (renderer instanceof RenderWithAttachmentPoints) {
                    RenderWithAttachmentPoints<AbstractTrainCarEntity> attachmentRenderer = (RenderWithAttachmentPoints) renderer;
                    Vec3 nextTPos = nextT.getPosition(pPartialTicks);
                    Vec3 tPos = t.getPosition(pPartialTicks);
                    Vec3 offset = nextTPos.subtract(tPos);
                    pose.translate(offset.x, offset.y, offset.z);
                    Pair<Vec3, Vec3> newAttachmentPoints = attachmentRenderer.renderCarAndGetAttachmentPoints(nextT, nextT.getYRot(), pPartialTicks, pose, buffer, pPackedLight);
                    Vec3 from = newAttachmentPoints.getFirst();
                    Vec3 to = attachmentPoints.getSecond();
                    pose.pushPose();
                    offset = from.subtract(nextTPos);
                    pose.translate(offset.x, offset.y, offset.z);
                    this.getAndRenderChain(nextT, from, to, pose, buffer, pPackedLight);
                    pose.popPose();
                    attachmentPoints = newAttachmentPoints;
                }
            }

            pose.popPose();
        }
    }

    private void getAndRenderChain(AbstractTrainCarEntity car, Vec3 from, Vec3 to, PoseStack matrixStack, MultiBufferSource buffer, int p_225623_6_) {
        matrixStack.pushPose();
        Vec3 vec = from.vectorTo(to);
        double dist = vec.length();
        int segments = (int) Math.ceil(dist * 4.0);
        matrixStack.mulPose(Vector3f.YP.rotation(-((float) Math.atan2(vec.z, vec.x))));
        matrixStack.mulPose(Vector3f.ZP.rotation((float) Math.asin(vec.y / dist)));
        matrixStack.pushPose();
        VertexConsumer ivertexbuilderChain = buffer.getBuffer(this.chainModel.renderType(CHAIN_TEXTURE));

        for (int i = 1; i < segments; ++i) {
            matrixStack.pushPose();
            matrixStack.translate((double) i / 4.0, 0.0, 0.0);
            this.chainModel.renderToBuffer(matrixStack, ivertexbuilderChain, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }

        matrixStack.popPose();
        matrixStack.popPose();
    }

    private Pair<Vec3, Vec3> getAttachmentPoints(Vec3 chainCentre, Vec3 trackDirection) {
        return new Pair(chainCentre.add(trackDirection.scale(0.2)), chainCentre.add(trackDirection.scale(-0.2)));
    }

    @ParametersAreNonnullByDefault
    public boolean shouldRender(T entity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    public Pair<Vec3, Vec3> renderCarAndGetAttachmentPoints(T car, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        Pair<Vec3, Vec3> attach = new Pair(car.getPosition(partialTicks).add(0.0, 0.44, 0.0), car.getPosition(partialTicks).add(0.0, 0.44, 0.0));
        pose.pushPose();
        long i = (long) car.getId() * 493286711L;
        i = i * i * 4392167121L + i * 98761L;
        float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        pose.translate(f, f1, f2);
        double d0 = Mth.lerp(partialTicks, car.xOld, car.getX());
        double d1 = Mth.lerp(partialTicks, car.yOld, car.getY());
        double d2 = Mth.lerp(partialTicks, car.zOld, car.getZ());
        double d3 = 0.30000001192092896;
        Vec3 pos = car.getPos(d0, d1, d2);
        float pitch = Mth.lerp(partialTicks, car.xRotO, car.getXRot());
        if (pos != null) {
            Vec3 forwardDir = car.getPosOffs(d0, d1, d2, 0.30000001192092896);
            Vec3 backDir = car.getPosOffs(d0, d1, d2, -0.30000001192092896);
            if (forwardDir == null) {
                forwardDir = pos;
            }

            if (backDir == null) {
                backDir = pos;
            }

            Vec3 centre = new Vec3(pos.x, (forwardDir.y + backDir.y) / 2.0, pos.z);
            Vec3 offset = centre.subtract(d0, d1, d2);
            pose.translate(offset.x, offset.y, offset.z);
            Vec3 trackDirection = forwardDir.subtract(backDir);
            if (trackDirection.length() != 0.0) {
                trackDirection = trackDirection.normalize();
                yaw = (float) (Math.atan2(-trackDirection.z, -trackDirection.x) * 180.0 / Math.PI + 90.0);
                pitch = (float) (Math.atan(-trackDirection.y) * 73.0);
            }

            Vec3 chainCentre = centre.add(0.0, 0.22, 0.0);
            attach = this.getAttachmentPoints(chainCentre, trackDirection);
        }

        pose.translate(0.0, 0.375, 0.0);
        pose.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
        pose.mulPose(Vector3f.XN.rotationDegrees(pitch));
        float f5 = (float) car.getHurtTime() - partialTicks;
        float f6 = car.getDamage() - partialTicks;
        if (f6 < 0.0F) {
            f6 = 0.0F;
        }

        if (f5 > 0.0F) {
            pose.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float) car.getHurtDir()));
        }

        pose.translate(0.0, 1.1, 0.0);
        pose.scale(-1.0F, -1.0F, 1.0F);
        this.entityModel.setupAnim(car, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = buffer.getBuffer(this.entityModel.renderType(this.getTextureLocation(car)));
        this.entityModel.renderToBuffer(pose, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        this.renderMana((LittleManaCarEntity) car, yaw, partialTicks, pose, buffer, packedLight);
        pose.popPose();
        return attach;
    }

    private void renderMana(LittleManaCarEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        int insideUVStart = 2;
        int insideUVEnd = 14;
        float manaLevel = (float) pEntity.getMana() / (float) pEntity.getMaxMana();

        if (manaLevel > 0) {
            pMatrixStack.pushPose();
            pMatrixStack.translate(-0.5F, Mth.clampedMap(manaLevel, 0, 1, 1F, 0.6875F), 0.5F);
            pMatrixStack.scale(1F, 1F, 1F);
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(270F));

            VertexConsumer buffer = pBuffer.getBuffer(RenderHelper.MANA_POOL_WATER);
            RenderHelper.renderIconCropped(pMatrixStack, buffer, insideUVStart, insideUVStart, insideUVEnd, insideUVEnd,
                    MiscellaneousModels.INSTANCE.manaWater.sprite(), 0xFFFFFF, 1, pPackedLight);

            pMatrixStack.popPose();
        }
    }

    protected Model getModel(T entity) {
        return this.entityModel;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }

}
