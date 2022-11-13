package me.xmrvizzy.skyblocker.skyblock.item;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.xmrvizzy.skyblocker.utils.NetworkUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy.Type;

public class WikiLookup {
    public static KeyBinding wikiLookup;
    public static KeyBinding wikiLookupOfficial;
    static MinecraftClient client = MinecraftClient.getInstance();
    static String id;
    public static Gson gson = new Gson();

    public static void init(){
        wikiLookup = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wikiLookup",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F1,
                "key.categories.skyblocker"
        ));
        wikiLookupOfficial = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wikiLookupOfficial",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                "key.categories.skyblocker"
        ));
    }

    public static String getSkyblockId(Slot slot) {
        //Grabbing the skyblock NBT data
        ItemStack selectedStack = slot.getStack();
        CompoundTag nbt = selectedStack.getSubTag("ExtraAttributes");
        if (nbt != null) {
            id = nbt.getString("id");
        }
        return id;
    }

    public static void openWiki(Slot slot){
        openWiki(slot, false);
    }
    public static void openWiki(Slot slot, Boolean official){
        if (Utils.isSkyblock){
            id = getSkyblockId(slot);
            int index;
            if(official)index=1;
            else index=0;
            try {
                //Setting up a connection with the repo
                String urlString = "https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/items/" + id + ".json";
                URL url = new URL(urlString);
                URLConnection request = url.openConnection(NetworkUtils.proxy());
                request.connect();

                //yoinking the wiki link
                JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
                JsonObject rootobj = root.getAsJsonObject();
                String wikiLink = rootobj.getAsJsonArray("info").get(index).getAsString();
                Util.getOperatingSystem().open(wikiLink);
            } catch (IOException | NullPointerException | IllegalStateException e) {
                if(index==1){
                    openWikiOfficial(slot);
                    client.player.sendMessage(Text.of("Can't locate a wiki article for this item in database, using /wiki command for wiki..."), false);
                }
                else{
                    e.printStackTrace();
                    client.player.sendMessage(Text.of("Can't locate a wiki article for this item..."), false);
                }
            }
        }
    }
    public static void openWikiOfficial(Slot slot){
        if (Utils.isSkyblock){
            id = getSkyblockId(slot);
            client.player.sendChatMessage("/wiki "+id);
        }
    }
}
