package me.xmrvizzy.skyblocker.skyblock;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Clipboard;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;

public class ClipboardChecker {
    static final Clipboard CLIPBOARD = new Clipboard();
    static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    static final ToastManager TOAST_MANAGER = CLIENT.getToastManager();
    static String lastClipboardString="";
    public static void tick(){
        String clipboardString = CLIPBOARD.getClipboard(CLIENT.getWindow().getHandle(), (error, description) -> {
            if (error != 65545) {
               CLIENT.getWindow().logGlError(error, description);
            }
         });
        if(lastClipboardString!=null && !lastClipboardString.equals(clipboardString)){
            lastClipboardString=clipboardString;
            onClipboardUpdate(lastClipboardString);
        }
    }
    private static void onClipboardUpdate(String clip) {
        if(SkyblockerConfig.get().general.pickupClipboardAuctionCommands &&(clip.startsWith("/ah ") || clip.startsWith("/viewauction "))){
            CLIENT.player.sendChatMessage(clip);
            TOAST_MANAGER.add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new LiteralText("Opening copied auction"), new LiteralText(clip.replace("/ah ", "").replace("/viewauction ", ""))));
        }
    }
}
