package com.leobeliik.littlebotanics.entity;

import com.leobeliik.littlebotanics.LittleBotanics;
import dev.murad.shipping.entity.custom.train.wagon.AbstractWagonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaPumpBlockEntity;
import vazkii.botania.xplat.XplatAbstractions;
import javax.annotation.ParametersAreNonnullByDefault;

public class LittleManaCarEntity extends AbstractWagonEntity {

    private static final int TRANSFER_RATE = 10000;
    private static final int MAX_MANA = 1000000;
    private static final String TAG_MANA = "mana";
    private static final EntityDataAccessor<Integer> MANA = SynchedEntityData.defineId(LittleManaCarEntity.class, EntityDataSerializers.INT);

    public LittleManaCarEntity(EntityType<LittleManaCarEntity> type, Level level) {
        super(type, level);
    }

    public LittleManaCarEntity(Level level, Double aDouble, Double aDouble1, Double aDouble2) {
        super(LittleBotanics.LITTLEMANACAR_ENTITY.get(), level, aDouble, aDouble1, aDouble2);
    }

    public ItemStack getPickResult() {
        return new ItemStack(LittleBotanics.LITTLEMANACAR_ITEM.get());
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
        return Type.RIDEABLE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MANA, 0);
    }

    @NotNull
    @Override
    public BlockState getDisplayBlockState() {
        return BotaniaBlocks.manaPool.defaultBlockState();
    }

    @ParametersAreNonnullByDefault
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    protected void applyNaturalSlowdown() {
        float f = 0.98F;
        this.setDeltaMovement(getDeltaMovement().multiply(f, 0, f));
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            double particleChance = 1F - (double) getMana() / (double) ManaPoolBlockEntity.MAX_MANA * 0.1;
            int color = ManaPoolBlockEntity.PARTICLE_COLOR;
            float green = (color >> 8 & 0xFF) / 255F;
            float blue = (color & 0xFF) / 255F;
            double x = Mth.floor(getX());
            double y = Mth.floor(getY());
            double z = Mth.floor(getZ());
            if (Math.random() > particleChance) {
                WispParticleData data = WispParticleData.wisp((float) Math.random() / 3F, 0, green, blue, 2F);
                level.addParticle(data, x + 0.3 + Math.random() * 0.5, y + 0.85 + Math.random() * 0.25, z + Math.random(), 0, (float) Math.random() / 25F, 0);
            }
        }
    }

    int getMana() {
        return entityData.get(MANA);
    }

    private void setMana(int mana) {
        entityData.set(MANA, mana);
    }

    int getMaxMana() {
        return MAX_MANA;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void moveAlongTrack(BlockPos pos, BlockState state) {
        super.moveAlongTrack(pos, state);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos pumpPos = pos.relative(dir);
            BlockState pumpState = level.getBlockState(pumpPos);
            if (pumpState.is(BotaniaBlocks.pump)) {
                ManaPumpBlockEntity pump = (ManaPumpBlockEntity) level.getBlockEntity(pumpPos);
                BlockPos poolPos = pumpPos.relative(dir);
                var receiver = XplatAbstractions.INSTANCE.findManaReceiver(level, poolPos, dir.getOpposite());

                if (receiver instanceof ManaPool pool && pump != null) {
                    Direction pumpDir = pumpState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    boolean did = false;
                    boolean can = false;

                    if (pumpDir == dir) { // Pool -> Car
                        can = true;

                        if (!pump.hasRedstone) {
                            int carMana = getMana();
                            int poolMana = pool.getCurrentMana();
                            int transfer = Math.min(TRANSFER_RATE, poolMana);
                            int actualTransfer = Math.min(ManaPoolBlockEntity.MAX_MANA - carMana, transfer);
                            if (actualTransfer > 0) {
                                pool.receiveMana(-transfer);
                                setMana(carMana + actualTransfer);
                                did = true;
                            }
                        }
                    } else if (pumpDir == dir.getOpposite()) { // Car -> Pool
                        can = true;

                        if (!pump.hasRedstone && !pool.isFull()) {
                            int carMana = getMana();
                            int transfer = Math.min(TRANSFER_RATE, carMana);
                            if (transfer > 0) {
                                pool.receiveMana(transfer);
                                setMana(carMana - transfer);
                                did = true;
                            }
                        }
                    }

                    if (did) {
                        pump.hasCart = true;
                        pump.setActive(true);
                    }

                    if (can) {
                        pump.hasCartOnTop = true;
                        pump.comparator = (int) ((double) getMana() / (double) ManaPoolBlockEntity.MAX_MANA * 15); // different from ManaPoolBlockEntity.calculateComparatorLevel, kept for compatibility
                    }

                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag cmp) {
        super.addAdditionalSaveData(cmp);
        cmp.putInt(TAG_MANA, getMana());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag cmp) {
        super.readAdditionalSaveData(cmp);
        setMana(cmp.getInt(TAG_MANA));
    }


    @SoftImplement("IForgeMinecart")
    public int getComparatorLevel() {
        return ManaPoolBlockEntity.calculateComparatorLevel(getMana(), MAX_MANA);
    }
}
