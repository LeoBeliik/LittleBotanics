package com.leobeliik.littlebotanics.entity.barges;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import static com.leobeliik.littlebotanics.LittleBotanics.MODID;

public class LittleManaBargeModel extends EntityModel<LittleManaBargeEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MODID, "littlemanabargemodel"), "main");
    private final ModelPart barge;
    private final ModelPart pool;

    public LittleManaBargeModel(ModelPart root) {
        this.barge = root.getChild("barge");
        this.pool = root.getChild("pool");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("pool", CubeListBuilder.create()
                        .texOffs(0, 19).addBox(-8.0F, -14.0F, -8.0F, 2.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(0, 19).addBox(6.0F, -14.0F, -8.0F, 2.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(2, 33).addBox(-5.0F, -14.0F, -8.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(1F, 0F, 0F)) //s
                        .texOffs(2, 33).addBox(-5.0F, -14.0F, 6.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(1F, 0F, 0F)) //s
                        .texOffs(-8, 23).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), //b
                PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition barge = partdefinition.addOrReplaceChild("barge", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0F, -27.0F, -7.0F, 12.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 23.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(LittleManaBargeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        barge.render(poseStack, buffer, packedLight, packedOverlay);
        pool.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
