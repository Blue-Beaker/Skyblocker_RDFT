package me.xmrvizzy.skyblocker.skyblock.dungeon;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig.Dungeons;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig.Dungeons.MapPosition;
import me.xmrvizzy.skyblocker.utils.ItemUtils;

public class DungeonMap {
    static final Dungeons DUNGEONS=SkyblockerConfig.get().locations.dungeons;
    static final MinecraftClient client = MinecraftClient.getInstance();
    public static void render(MatrixStack matrices) {
        if (client.player == null && client.world == null) return;
        ItemStack item = client.player.inventory.main.get(8);
        //CompoundTag tag = item.getTag();

        //if (tag != null && tag.contains("map")) {
        if ("filled_map".equals(ItemUtils.getId(item))) {
            VertexConsumerProvider.Immediate vertices = client.getBufferBuilders().getEffectVertexConsumers();
            MapRenderer map = client.gameRenderer.getMapRenderer();
            MapState state = FilledMapItem.getMapState(item, client.world);

            if (state == null) return;
            float mapX,mapY;
            if(DUNGEONS.mapPosition==MapPosition.UPPER_LEFT){
                mapX=DUNGEONS.mapX;
                mapY=DUNGEONS.mapY;
            }else if(DUNGEONS.mapPosition==MapPosition.UPPER_RIGHT){
                mapX=client.getWindow().getScaledWidth()-DUNGEONS.mapX-128;
                mapY=DUNGEONS.mapY;
            }else if(DUNGEONS.mapPosition==MapPosition.LOWER_LEFT){
                mapX=DUNGEONS.mapX;
                mapY=client.getWindow().getScaledHeight()-DUNGEONS.mapY-128;
            }else{
                mapX=client.getWindow().getScaledWidth()-DUNGEONS.mapX-128;
                mapY=client.getWindow().getScaledHeight()-DUNGEONS.mapY-128;
            }
            matrices.push();
            matrices.translate(mapX, mapY, 0);
            matrices.scale(DUNGEONS.mapScale, DUNGEONS.mapScale, 0);
            map.draw(matrices, vertices, state, false, 15728880);
            vertices.draw();
            matrices.pop();
        }
    }
}