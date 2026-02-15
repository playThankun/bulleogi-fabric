package com.thankun.bulleogi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Bulleogi implements ModInitializer {
    public static final String MOD_ID = "bulleogi";
    
    // 탭에 넣을 아이템들을 담을 리스트입니닿!
    private static final List<Item> MINECRAFT_ITEMS = new ArrayList<>();

    // 1. 공기 블록 정의 (마크 소속으로 등록!)
    public static final Block AIR_BUBBLE_UP = new AirBubbleColumnBlock(
            AbstractBlock.Settings.copy(Blocks.BUBBLE_COLUMN).noCollision().nonOpaque()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "air_bubble_up_block")))
    );
    public static final Block AIR_BUBBLE_DOWN = new AirBubbleColumnBlock(
            AbstractBlock.Settings.copy(Blocks.BUBBLE_COLUMN).noCollision().nonOpaque()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("minecraft", "air_bubble_down_block")))
    );

    @Override
    public void onInitialize() {
        // 2. 블록 등록 (네임스페이스: minecraft)
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "air_bubble_up_block"), AIR_BUBBLE_UP);
        Registry.register(Registries.BLOCK, Identifier.of("minecraft", "air_bubble_down_block"), AIR_BUBBLE_DOWN);

        // 3. 아이템 등록 및 리스트 추가 (네임스페이스: minecraft)
        MINECRAFT_ITEMS.add(registerMinecraftItem("set_air", Blocks.AIR.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("nether_portal_spawner", Blocks.NETHER_PORTAL.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("end_portal_spawner", Blocks.END_PORTAL.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("end_gateway_spawner", Blocks.END_GATEWAY.getDefaultState()));
        MINECRAFT_ITEMS.add(registerMinecraftItem("frosted_ice", Blocks.FROSTED_ICE.getDefaultState()));
        
        // 버블 칼럼 등록
        MINECRAFT_ITEMS.add(registerMinecraftItem("air_bubble_up", AIR_BUBBLE_UP.getDefaultState().with(BubbleColumnBlock.DRAG, false)));
        MINECRAFT_ITEMS.add(registerMinecraftItem("air_bubble_down", AIR_BUBBLE_DOWN.getDefaultState().with(BubbleColumnBlock.DRAG, true)));

        // 4. [도구 및 유용한 물건] 탭에 아이템들 쑤셔넣기!
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            for (Item item : MINECRAFT_ITEMS) {
                content.add(item);
            }
        });
    }

    private static Item registerMinecraftItem(String path, BlockState state) {
        // 네임페이스 이스 마인크래프트!
        Identifier id = Identifier.of("minecraft", path); 
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings().registryKey(key);
        
        BlockSetterItem item = new BlockSetterItem(state, settings);
        return Registry.register(Registries.ITEM, key, item);
    }
}

class BlockSetterItem extends Item {
    private final BlockState targetState;

    public BlockSetterItem(BlockState state, Item.Settings settings) { 
        super(settings); 
        this.targetState = state; 
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        // 공기 설치면 그 자리, 나머지는 클릭한 면의 앞칸!
        BlockPos pos = (this.targetState.isOf(Blocks.AIR)) 
                       ? context.getBlockPos() 
                       : context.getBlockPos().offset(context.getSide());
        
        if (!world.isClient()) { 
            world.setBlockState(pos, this.targetState, 3); 
        }
        return ActionResult.SUCCESS;
    }
}