package com.leobeliik.littlebotanics.entity.barges;

import com.leobeliik.littlebotanics.LittleBotanics;
import dev.murad.shipping.block.dock.BargeDockTileEntity;
import dev.murad.shipping.entity.custom.vessel.barge.AbstractBargeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaPumpBlockEntity;
import vazkii.botania.xplat.XplatAbstractions;
import javax.annotation.ParametersAreNonnullByDefault;

public class LittleManaBargeEntity extends AbstractBargeEntity {

    private static final int TRANSFER_RATE = 10000;
    private static final int MAX_MANA = 1000000;
    private static final String TAG_MANA = "mana";
    private static final EntityDataAccessor<Integer> MANA = SynchedEntityData.defineId(LittleManaBargeEntity.class, EntityDataSerializers.INT);

    public LittleManaBargeEntity(EntityType<? extends LittleManaBargeEntity> type, Level level) {
        super(type, level);
    }

    public LittleManaBargeEntity(Level worldIn, double x, double y, double z) {
        super(LittleBotanics.LITTLEMANABARGE_ENTITY.get(), worldIn, x, y, z);
    }

    @Override
    public Item getDropItem() {
        return new ItemStack(LittleBotanics.LITTLEMANABARGE_ITEM.get()).getItem();
    }

    @Override
    protected void doInteract(Player player) {
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(MANA, 0);
    }

    @ParametersAreNonnullByDefault
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
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
        } else {
            this.PumpSearch(this.getBlockPos());
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

    private void PumpSearch(BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos pumpPos = pos.relative(dir).offset(0, 1, 0);
            BlockState pumpState = level.getBlockState(pumpPos);
            if (pumpState.is(BotaniaBlocks.pump)) {
                ManaPumpBlockEntity pump = (ManaPumpBlockEntity) level.getBlockEntity(pumpPos);
                BlockPos poolPos = pumpPos.relative(dir);
                var receiver = XplatAbstractions.INSTANCE.findManaReceiver(level, poolPos, dir.getOpposite());

                if (receiver instanceof ManaPool pool && pump != null) {
                    Direction pumpDir = pumpState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    boolean did = false;
                    boolean can = false;
                    boolean shoulDock = level.getBlockEntity(pumpPos.below()) instanceof BargeDockTileEntity;

                    System.out.println();

                    if (pumpDir == dir) { // Pool -> Car
                        can = true;

                        if (!pump.hasRedstone) {
                            int carMana = getMana();
                            int poolMana = pool.getCurrentMana();
                            int transfer = Math.min(TRANSFER_RATE, poolMana);
                            int actualTransfer = Math.min(ManaPoolBlockEntity.MAX_MANA - carMana, transfer);
                            if (actualTransfer > 0) {
                                if (shoulDock) {
                                    dock();
                                }
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
                                if (shoulDock) {
                                    dock();
                                }
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

    private void dock() {
        //TODO add sound
        this.getTrain().asList().forEach(tug -> {
            tug.setDeltaMovement(Vec3.ZERO);
            tug.moveTo((int) Math.floor(tug.getX()) + 0.5, tug.getY(), (int) Math.floor(tug.getZ()) + 0.5);
            this.moveTo((int) Math.floor(this.getX()) + 0.5, this.getY(), (int) Math.floor(this.getZ()) + 0.5);
        });
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag cmp) {
        super.addAdditionalSaveData(cmp);
        cmp.putInt(TAG_MANA, getMana());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag cmp) {
        super.readAdditionalSaveData(cmp);
        setMana(cmp.getInt(TAG_MANA));
    }


    @SoftImplement("IForgeMinecart")
    public int getComparatorLevel() {
        return ManaPoolBlockEntity.calculateComparatorLevel(getMana(), MAX_MANA);
    }
}
