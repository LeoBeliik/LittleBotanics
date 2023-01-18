package com.leobeliik.littlebotanics;

import com.leobeliik.littlebotanics.entity.barges.LittleManaBargeEntity;
import com.leobeliik.littlebotanics.entity.barges.LittleManaBargeModel;
import com.leobeliik.littlebotanics.entity.barges.LittleManaBargeRenderer;
import com.leobeliik.littlebotanics.entity.cars.LittleManaCarEntity;
import com.leobeliik.littlebotanics.entity.cars.LittleManaCarModel;
import com.leobeliik.littlebotanics.entity.cars.LittleManaCarRenderer;
import com.leobeliik.littlebotanics.items.LittleManaCarItem;
import dev.murad.shipping.item.VesselItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
        bus.addListener(this::addEntityAttributes);
        Registry(bus);
        clientRegistry(bus);
    }

    private void Registry(IEventBus bus) {
        ITEMS.register(bus);
        ENTITIES.register(bus);
    }

    private void clientRegistry(IEventBus bus) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerRenders));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerLayers));
    }

    //ITEMS regs
    public static final RegistryObject<Item> LITTLEMANACAR_ITEM = ITEMS.register("littlemanacar_item", () ->
            new LittleManaCarItem(LittleManaCarEntity::new, new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));

    public static final RegistryObject<Item> LITTLEMANABARGE_ITEM = ITEMS.register("littlemanabarge_item", () ->
            new VesselItem(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION), LittleManaBargeEntity::new));

    //ENTITIES regs
    public static final RegistryObject<EntityType<LittleManaCarEntity>> LITTLEMANACAR_ENTITY = ENTITIES.register("littlemanacar", () ->
            EntityType.Builder.<LittleManaCarEntity>of(LittleManaCarEntity::new, MobCategory.MISC)
                    .sized(0.7f, 0.9f)
                    .clientTrackingRange(8)
                    .setShouldReceiveVelocityUpdates(true)
                    .build(new ResourceLocation(MODID, "littlemanacar").toString()));

    public static final RegistryObject<EntityType<LittleManaBargeEntity>> LITTLEMANABARGE_ENTITY = ENTITIES.register("littlemanabarge", () ->
            EntityType.Builder.<LittleManaBargeEntity>of(LittleManaBargeEntity::new, MobCategory.MISC)
            .sized(0.6f, 0.9f)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "littlemanabarge").toString()));

    //OTHER
    @SubscribeEvent
    public void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(LITTLEMANABARGE_ENTITY.get(), LittleManaBargeEntity.setCustomAttributes().build());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(LITTLEMANACAR_ENTITY.get(), ctx -> new LittleManaCarRenderer<>(ctx,
                LittleManaCarModel::new,
                LittleManaCarModel.LAYER_LOCATION,
                "textures/entity/littlemanacar.png"));

        event.registerEntityRenderer(LITTLEMANABARGE_ENTITY.get(), ctx -> new LittleManaBargeRenderer<>(ctx,
                LittleManaBargeModel::new,
                LittleManaBargeModel.LAYER_LOCATION,
                new ResourceLocation(MODID, "textures/entity/littlemanabarge.png")));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LittleManaCarModel.LAYER_LOCATION, LittleManaCarModel::createBodyLayer);
        event.registerLayerDefinition(LittleManaBargeModel.LAYER_LOCATION, LittleManaBargeModel::createBodyLayer);
    }
}
