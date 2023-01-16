package com.leobeliik.littlebotanica;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(LittleBotanica.MODID)
public class LittleBotanica {
    public static final String MODID = "littlebotanica";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public LittleBotanica() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //bus.addListener(this::registerRenderers);
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerRenderers));
        //bus.addListener(this::clientRegistry);
        //Config.init();
        Registry();
    }

    private void Registry() {
        //Network.registerMessages();
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITY.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
