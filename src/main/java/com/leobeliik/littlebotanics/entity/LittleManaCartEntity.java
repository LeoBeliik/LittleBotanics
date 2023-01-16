package com.leobeliik.littlebotanics.entity;

import com.leobeliik.littlebotanics.LittleBotanics;
import dev.murad.shipping.entity.custom.train.wagon.AbstractWagonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LittleManaCartEntity extends AbstractWagonEntity {

    public LittleManaCartEntity(EntityType<LittleManaCartEntity> type, Level level) {
        super(type, level);
    }

    public LittleManaCartEntity(Level level, Double aDouble, Double aDouble1, Double aDouble2) {
        super(LittleBotanics.LITTLEMANACART_ENTITY.get(), level, aDouble, aDouble1, aDouble2);
    }

    public ItemStack getPickResult() {
        return new ItemStack(LittleBotanics.LITTLEMANACART_ITEM.get());
    }

    private void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entity.yRotO += f1 - f;
        entity.setYRot(entity.getYRot() + f1 - f);
        entity.setYHeadRot(entity.getYRot());
    }

    public AbstractMinecart.Type getMinecartType() {
        return Type.CHEST;
    }
}
