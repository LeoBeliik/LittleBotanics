package com.leobeliik.littlebotanics.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.ParametersAreNonnullByDefault;
import static com.leobeliik.littlebotanics.LittleBotanics.MODID;

public class LittleManaCarModel extends EntityModel<LittleManaCarEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(MODID, "littlemanacarmodel"), "main");
    private final ModelPart car;
    private final ModelPart pool;

    public LittleManaCarModel(ModelPart root) {
        this.car = root.getChild("car");
        this.pool = root.getChild("pool");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("pool", CubeListBuilder.create()
                        //.texOffs(26, 18).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F, 0.0F, 1.0F))
                        //pool
                        .texOffs(32, 12).addBox(-8.0F, -14.0F, -8.0F, 2.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(32, 12).addBox(6.0F, -14.0F, -8.0F, 2.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(34, 26).addBox(-5.0F, -14.0F, -8.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(1F, 0F, 0F)) //s
                        .texOffs(34, 26).addBox(-5.0F, -14.0F, 6.0F, 10.0F, 8.0F, 2.0F, new CubeDeformation(1F, 0F, 0F)) //s
                        .texOffs(24, 16).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)), //b
                PartPose.offset(0.0F, 24.0F, 0.0F));


        PartDefinition car = partdefinition.addOrReplaceChild("car", CubeListBuilder.create()
                        //straps
                        //walls
                        .texOffs(0, 0).addBox(-7.0F, -6.0F, -8.0F, 2.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(0, 0).addBox(5.0F, -6.0F, -8.0F, 2.0F, 5.0F, 16.0F, new CubeDeformation(0.0F)) //l
                        .texOffs(0, 28).addBox(-5.0F, -6.0F, -8.0F, 10.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)) //s
                        .texOffs(0, 28).addBox(-5.0F, -6.0F, 6.0F, 10.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)) //s
                        //bottom
                        .texOffs(20, 0).addBox(-5.0F, -6.0F, -6.0F, 10.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                        //wheels
                        .texOffs(0, 0).addBox(-6.0F, -2.0F, 4.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(5.0F, -2.0F, 4.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(5.0F, -2.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @ParametersAreNonnullByDefault
    public void setupAnim(LittleManaCarEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @ParametersAreNonnullByDefault
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.car.render(poseStack, buffer, packedLight, packedOverlay);
        this.pool.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
