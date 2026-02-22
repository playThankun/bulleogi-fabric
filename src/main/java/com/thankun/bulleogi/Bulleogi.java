package com.thankun.bulleogi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Bulleogi implements ModInitializer {
    public static final String MOD_ID = "bulleogi";
    
    private static final List<Item> MINECRAFT_ITEMS = new ArrayList<>();
    private static final List<Item> ADDS_ONLY_ITEMS = new ArrayList<>();

    public static final Block AIR_BUBBLE_UP = new AirBubbleColumnBlock(AbstractBlock.Settings.copy(Blocks.BUBBLE_COLUMN).noCollision().nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "air_bubble_up_block")))) {
    @Override
    protected void appendProperties(net.minecraft.state.StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }
    // 생성자에서 기본 상태를 DRAG=false로 0.1초 만에 고정!!!
    {
        setDefaultState(this.stateManager.getDefaultState().with(BubbleColumnBlock.DRAG, false));
    }
};

public static final Block AIR_BUBBLE_DOWN = new AirBubbleColumnBlock(AbstractBlock.Settings.copy(Blocks.BUBBLE_COLUMN).noCollision().nonOpaque().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "air_bubble_down_block")))) {
    {
        setDefaultState(this.stateManager.getDefaultState().with(BubbleColumnBlock.DRAG, true));
    }
};
    public static final Block DEBUG = new Block(AbstractBlock.Settings.copy(Blocks.STRUCTURE_BLOCK).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "debug"))));
    public static final Block DEBUG2 = new Block(AbstractBlock.Settings.copy(Blocks.JIGSAW).registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "debug2"))));

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "air_bubble_up_block"), AIR_BUBBLE_UP);
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "air_bubble_down_block"), AIR_BUBBLE_DOWN);
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "debug"), DEBUG);
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "debug2"), DEBUG2);

        //아이템 등록
        MINECRAFT_ITEMS.add(registerMinecraftItem("set_air", Blocks.AIR.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("nether_portal_spawner", Blocks.NETHER_PORTAL.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("end_portal_spawner", Blocks.END_PORTAL.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("end_gateway_spawner", Blocks.END_GATEWAY.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("frosted_ice", Blocks.FROSTED_ICE.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("bubble_column_and_water_up", Blocks.BUBBLE_COLUMN.getDefaultState().with(BubbleColumnBlock.DRAG, false)));
        MINECRAFT_ITEMS.add(registerMinecraftItem("bubble_column_and_water_down", Blocks.BUBBLE_COLUMN.getDefaultState().with(BubbleColumnBlock.DRAG, true)));

        Item debugItem = registerMinecraftItem("debug", DEBUG.getDefaultState());
        MINECRAFT_ITEMS.add(debugItem);
        ADDS_ONLY_ITEMS.add(debugItem);

        Item debug2Item = registerMinecraftItem("debug2", DEBUG2.getDefaultState());
        MINECRAFT_ITEMS.add(debug2Item);
        ADDS_ONLY_ITEMS.add(debug2Item);

        Item bubbleUpItem = registerMinecraftItem("air_bubble_up", AIR_BUBBLE_UP.getDefaultState().with(BubbleColumnBlock.DRAG, false));
        MINECRAFT_ITEMS.add(bubbleUpItem);
        ADDS_ONLY_ITEMS.add(bubbleUpItem);

        Item bubbleDownItem = registerMinecraftItem("air_bubble_down", AIR_BUBBLE_DOWN.getDefaultState().with(BubbleColumnBlock.DRAG, true));
        MINECRAFT_ITEMS.add(bubbleDownItem);
        ADDS_ONLY_ITEMS.add(bubbleDownItem);

        //aaaaaaaaaaaaaaaaaaaaaaaa
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "aaa_main"),
            FabricItemGroup.builder()
                .icon(() -> new ItemStack(bubbleUpItem))
                .displayName(Text.translatable("itemGroup.bulleogi.main"))
                .entries((displayContext, entries) -> MINECRAFT_ITEMS.forEach(entries::add))
                .build()
        );

        //zzzzzzzzzzzzzzzzzzzzzzzz
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "zzz_adds"),
            FabricItemGroup.builder()
                .icon(() -> new ItemStack(debugItem))
                .displayName(Text.translatable("itemGroup.bulleogi.adds"))
                .entries((displayContext, entries) -> ADDS_ONLY_ITEMS.forEach(entries::add))
                .build()
        );
    }

    private static Item registerMinecraftItem(String path, BlockState state) {
        Identifier id = Identifier.of("minecraft", path); 
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings().registryKey(key).translationKey("item.minecraft." + path);
        return Registry.register(Registries.ITEM, key, new BlockSetterItem(state, settings));
    }
}

class BlockSetterItem extends Item {
    private final BlockState targetState;
    public BlockSetterItem(BlockState state, Item.Settings settings) { super(settings); this.targetState = state; }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient()) { 
            BlockPos pos = (this.targetState.isOf(Blocks.AIR)) ? context.getBlockPos() : context.getBlockPos().offset(context.getSide());
            world.setBlockState(pos, this.targetState, 3); 
        }
        return ActionResult.SUCCESS;
    }
}