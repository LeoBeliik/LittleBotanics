package com.leobeliik.littlebotanics.mixins;

import com.leobeliik.littlebotanics.entity.barges.LittleManaBargeEntity;
import com.leobeliik.littlebotanics.entity.cars.LittleManaCarEntity;
import dev.murad.shipping.block.dock.AbstractTailDockTileEntity;
import dev.murad.shipping.block.dock.DockingBlockStates;
import dev.murad.shipping.util.LinkableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.block_entity.mana.ManaPumpBlockEntity;

@Mixin(AbstractTailDockTileEntity.class)
public abstract class AbstractTailDockTileEntityMixin<T extends Entity & LinkableEntity<T>> {

    @Inject(method = "Ldev/murad/shipping/block/dock/AbstractTailDockTileEntity;checkInterface(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)Ljava/lang/Boolean;",
            at = @At("RETURN"), remap = false)
    private Boolean littleBotanics_checkPumpOnDock(T vessel, BlockPos p, CallbackInfoReturnable<Boolean> cir) {
        if (vessel instanceof LittleManaBargeEntity && LittleManaBargeEntity.shouldDock()) {
            //if in insert mode, default check is in front and below the dock, this puts it on top of the dock; gotta ignore rail mode, sorry
            return this.isInsert() ? checkDirections(vessel, p.above(2)) : vessel.level.getBlockEntity(p) instanceof ManaPumpBlockEntity;
        }
        if (vessel instanceof LittleManaCarEntity && LittleManaCarEntity.shouldDock()) {
            //if in insert mode, default check is the rail, this puts it on the side of the dock; gotta ignore rail mode, sorry
            return this.isInsert() ? checkDirections(vessel, p.above()) : vessel.level.getBlockEntity(p) instanceof ManaPumpBlockEntity;
        }
        return cir.getReturnValue();
    }

    @Unique
    private Boolean checkDirections(T vessel, BlockPos p) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (vessel.level.getBlockEntity(p.relative(dir)) instanceof ManaPumpBlockEntity) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private Boolean isInsert() {
        return ((AbstractTailDockTileEntity) (Object) this).getBlockState().getValue(DockingBlockStates.INVERTED);
    }
}
