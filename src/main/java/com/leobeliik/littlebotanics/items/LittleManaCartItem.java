package com.leobeliik.littlebotanics.items;

import com.mojang.datafixers.util.Function4;
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity;
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class LittleManaCartItem extends Item {

    private final Function4<Level, Double, Double, Double, AbstractTrainCarEntity> constructor;

    public LittleManaCartItem(Function4<Level, Double, Double, Double, AbstractTrainCarEntity> constructor, Item.Properties pProperties) {
        super(pProperties);
        this.constructor = constructor;
    }

    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        } else {
            ItemStack itemstack = pContext.getItemInHand();
            if (!level.isClientSide) {
                RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
                double d0 = 0.0;
                if (railshape.isAscending()) {
                    d0 = 0.5;
                }

                AbstractMinecart abstractminecart = this.constructor.apply(level, (double) blockpos.getX() + 0.5, (double) blockpos.getY() + 0.0625 + d0, (double) blockpos.getZ() + 0.5);
                if (itemstack.hasCustomHoverName()) {
                    abstractminecart.setCustomName(itemstack.getHoverName());
                }

                if (pContext.getPlayer() != null && abstractminecart.getDirection().equals(pContext.getPlayer().getDirection()) && abstractminecart instanceof AbstractLocomotiveEntity) {
                    AbstractLocomotiveEntity l = (AbstractLocomotiveEntity) abstractminecart;
                    l.flip();
                }

                level.addFreshEntity(abstractminecart);
                level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }
}
