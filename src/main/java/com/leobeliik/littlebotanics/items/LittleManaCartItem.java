package com.leobeliik.littlebotanics.items;

import com.mojang.datafixers.util.Function4;
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity;
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class LittleManaCartItem extends Item {

    private final Function4<Level, Double, Double, Double, AbstractTrainCarEntity> constructor;

    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        public @NotNull ItemStack execute(BlockSource p_42949_, @NotNull ItemStack p_42950_) {
            Direction direction = p_42949_.getBlockState().getValue(DispenserBlock.FACING);
            Level level = p_42949_.getLevel();
            double d0 = p_42949_.x() + (double) direction.getStepX() * 1.125;
            double d1 = Math.floor(p_42949_.y()) + (double) direction.getStepY();
            double d2 = p_42949_.z() + (double) direction.getStepZ() * 1.125;
            BlockPos blockpos = p_42949_.getPos().relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (blockstate.is(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    d3 = 0.6;
                } else {
                    d3 = 0.1;
                }
            } else {
                if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.defaultDispenseItemBehavior.dispense(p_42949_, p_42950_);
                }

                BlockState blockstate1 = level.getBlockState(blockpos.below());
                RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? blockstate1.getValue(((BaseRailBlock) blockstate1.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railshape1.isAscending()) {
                    d3 = -0.4;
                } else {
                    d3 = -0.9;
                }
            }

            AbstractMinecart abstractminecart = ((LittleManaCartItem) p_42950_.getItem()).constructor.apply(level, d0, d1 + d3, d2);
            if (p_42950_.hasCustomHoverName()) {
                abstractminecart.setCustomName(p_42950_.getHoverName());
            }

            level.addFreshEntity(abstractminecart);
            p_42950_.shrink(1);
            return p_42950_;
        }

        protected void playSound(BlockSource p_42947_) {
            p_42947_.getLevel().levelEvent(1000, p_42947_.getPos(), 0);
        }
    };

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
