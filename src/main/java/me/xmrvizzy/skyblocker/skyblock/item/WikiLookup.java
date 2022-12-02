package me.xmrvizzy.skyblocker.skyblock.item;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import me.xmrvizzy.skyblocker.utils.NetworkUtils;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WikiLookup {
    private static ExecutorService pool = Executors.newSingleThreadExecutor();
    public static KeyBinding wikiLookup;
    public static KeyBinding wikiLookupOfficial;
    static MinecraftClient client = MinecraftClient.getInstance();
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
        String id = null;
        if (nbt != null) {
            id = nbt.getString("id");
        }
        return id;
    }

    public static void openWiki(Slot slot, Boolean official){
        pool.execute(new Runnable() {
            @Override
            public void run() {
                openWikiOriginal(slot, official);
            }
        });
    }
    public static void openWikiOriginal(Slot slot, Boolean official){
        if (Utils.isSkyblock){
            String id = getSkyblockId(slot);
            if(id==null){
                client.player.sendMessage(new LiteralText("Not a skyblock Item!").formatted(Formatting.RED), false);
                return;
            }
            int index;
            if(official)index=1;
            else index=0;
            try {
                //Setting up a connection with the repo
                String urlString = "https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/items/" + id + ".json";
                URL url = new URL(urlString);
                URLConnection request = url.openConnection(NetworkUtils.proxy());
                request.connect();

                //yoinking the wiki link]
                int length = request.getContentLength();
                StringBuffer buffer = new StringBuffer(length);
                BufferedInputStream reader = new BufferedInputStream((InputStream)request.getContent());
                /* 
                for(int i=0;i<length;i++){
                    buffer.append((char)reader.read());
                }
                reader.close();*/
                String jsonString = String.valueOf(buffer);
                JsonObject rootobj;
                try{
                    rootobj = JsonParser.parseReader(new JsonReader(new InputStreamReader((InputStream)request.getContent()))).getAsJsonObject();
                }catch(NoSuchMethodError e){
                    rootobj = new JsonParser().parse(new JsonReader(new InputStreamReader((InputStream)request.getContent()))).getAsJsonObject();
                }
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
            String id = getSkyblockId(slot);
            client.player.sendChatMessage("/wiki "+id);
        }
    }
}
