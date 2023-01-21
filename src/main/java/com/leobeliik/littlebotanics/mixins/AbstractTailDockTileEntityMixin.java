package com.leobeliik.littlebotanics.mixins;

import com.leobeliik.littlebotanics.entity.barges.LittleManaBargeEntity;
import com.leobeliik.littlebotanics.entity.cars.LittleManaCarEntity;
import dev.murad.shipping.block.dock.AbstractTailDockTileEntity;
import dev.murad.shipping.block.dock.DockingBlockStates;
import dev.murad.shipping.util.LinkableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractTailDockTileEntity.class)
public abstract class AbstractTailDockTileEntityMixin<T extends Entity & LinkableEntity<T>> {

    @Inject(method = "Ldev/murad/shipping/block/dock/AbstractTailDockTileEntity;checkInterface(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)Ljava/lang/Boolean;",
            at = @At("RETURN"), remap = false)
    private Boolean littleBotanics_checkPumpOnDock(T vessel, BlockPos p, CallbackInfoReturnable<Boolean> cir) {
        //gotta ignore rail mode, sorry
        if (vessel instanceof LittleManaBargeEntity barge) {
           return barge.shouldDock(this.isInsert(), p);
        } else if (vessel instanceof LittleManaCarEntity car) {
            return car.shouldDock(this.isInsert(), p);
        }
        return cir.getReturnValue();
    }

    @Unique
    private Boolean isInsert() {
        //this is the same as AbstractTailDockTileEntity#isExtracted() but shadowing that didn't work so I made my own
        return ((AbstractTailDockTileEntity) (Object) this).getBlockState().getValue(DockingBlockStates.INVERTED);
    }
}
