package com.leobeliik.littlebotanics;

import com.leobeliik.littlebotanics.entity.LittleManaCarEntity;
import com.leobeliik.littlebotanics.entity.LittleManaCarModel;
import com.leobeliik.littlebotanics.entity.LittleManaCarRenderer;
import com.leobeliik.littlebotanics.items.LittleManaCarItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(LittleBotanics.MODID)
public class LittleBotanics {
    public static final String MODID = "littlebotanics";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public LittleBotanics() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry(bus);
        clientRegistry(bus);
    }

    private void Registry(IEventBus bus) {
        //Network.registerMessages();
        ITEMS.register(bus);
        ENTITIES.register(bus);
    }

    private void clientRegistry(IEventBus bus) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerRenders));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerLayers));
    }

    public static final RegistryObject<Item> LITTLEMANACAR_ITEM = ITEMS.register("littlemanacar_item", () ->
            new LittleManaCarItem(LittleManaCarEntity::new, new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1)));

    public static final RegistryObject<EntityType<LittleManaCarEntity>> LITTLEMANACAR_ENTITY =
            ENTITIES.register("littlemanacar", () ->
                    EntityType.Builder.<LittleManaCarEntity>of(LittleManaCarEntity::new, MobCategory.MISC)
                            .sized(0.7f, 0.9f)
                            .clientTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(new ResourceLocation(MODID, "littlemanacar").toString()));

    @SubscribeEvent
    public void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LITTLEMANACAR_ENTITY.get(), ctx -> new LittleManaCarRenderer<>(ctx,
                LittleManaCarModel::new,
                LittleManaCarModel.LAYER_LOCATION,
                "textures/entity/littlemanacar.png"));
    }

    @SubscribeEvent
    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LittleManaCarModel.LAYER_LOCATION, LittleManaCarModel::createBodyLayer);
    }
}
