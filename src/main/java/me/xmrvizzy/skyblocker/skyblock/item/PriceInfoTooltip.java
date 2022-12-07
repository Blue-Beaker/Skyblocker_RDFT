package me.xmrvizzy.skyblocker.skyblock.item;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.GZIPInputStream;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.utils.ItemUtils;
import me.xmrvizzy.skyblocker.utils.NetworkUtils;

public class PriceInfoTooltip {
    private JsonObject auctionPricesJson = null;
    private JsonObject bazaarPricesJson = null;
    public static JsonObject prices = PriceInfoTooltip.downloadPrices();
    static MinecraftClient client = MinecraftClient.getInstance();
    public static void onInjectTooltip(ItemStack stack, TooltipContext context, List<Text> list) {
        if(!SkyblockerConfig.get().items.priceInfoTooltip) return;
        String name = getInternalNameForItem(stack);
        try {
            if(SkyblockerConfig.get().general.readableBazaarGraphs && "paper".equals(ItemUtils.getId(stack)) && getInternalNameForItem(stack)==null){
                readableBaaarGraphs(list);
            }
            if(!list.toString().contains("Avg. BIN Price") && prices.has(name) ){
                if(prices != null){
                
                    JsonElement getPrice = prices.get(name);
                    String price = round(getPrice.getAsDouble(), 2);
                   
                    list.add(new LiteralText("Avg. BIN Price: ").formatted(Formatting.GOLD).append(new LiteralText(price + " Coins").formatted(Formatting.DARK_AQUA)));
                }
            }
        }catch(Exception e){
            if(prices!=null)
            client.player.sendMessage(new LiteralText(e.toString()), false);
        }
    
	}
    public static String round(double value, int places) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return df.format(bd);
    }
    public static String getInternalNameForItem(ItemStack stack) {
        if(stack == null) return null;
        CompoundTag tag = stack.getTag();
        return getInternalnameFromNBT(tag);
    }

    public static String getInternalnameFromNBT(CompoundTag tag) {
        String internalname = null;
        if(tag != null && tag.contains("ExtraAttributes", 10)) {
            CompoundTag  ea = tag.getCompound("ExtraAttributes");

            if(ea.contains("id", 8)) {
                internalname = ea.getString("id").replaceAll(":", "-");
            } else {
                return null;
            }


            if("ENCHANTED_BOOK".equals(internalname)) {
                CompoundTag enchants = ea.getCompound("enchantments");

                for(String enchname : enchants.getKeys()) {
                    internalname = enchname.toUpperCase() + ";" + enchants.getInt(enchname);
                    break;
                }
            }
        }

        return internalname;
    }

    public static JsonObject downloadPrices() {
        try {
            if(!SkyblockerConfig.get().items.priceInfoTooltip) return null;
            downloadUsingStream("https://moulberry.codes/auction_averages_lbin/3day.json.gz", "3day.json.gz");
            decompressGzipFile("3day.json.gz", "3day.json");
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("3day.json"));
              // convert JSON file to map
            JsonObject  map = gson.fromJson(reader, JsonObject.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void decompressGzipFile(String gzipFile, String newFile) {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        URLConnection request = url.openConnection(NetworkUtils.proxy());
        request.connect();
        BufferedInputStream bis = new BufferedInputStream((InputStream) request.getContent());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
    public static void readableBaaarGraphs(List<Text> list){
        for(int i=0; i<list.size();i++){
            Text line = list.get(i);
            replaceAllInText(line);
            //if(line.getString().contains("·")){
            //    list.set(i, new LiteralText(line.getString().replace("·", "-").replace("+", "│")).setStyle(line.getStyle()));
            //}
        }
    }
    public static Text replaceAllInText(Text line){
        List<Text> texts = line.getSiblings();
        if(texts.size()>0){
            for(int j=0;j<texts.size();j++){
                Text text = texts.get(j);
                if(text.asString().contains("·")){
                    texts.set(j, new LiteralText(text.asString().replace("·","-")).setStyle(text.getStyle()));
                }
                //else if(text.asString().contains("+")){
                //    texts.set(j, new LiteralText(text.asString().replace("+","│")).setStyle(text.getStyle()));
                //}
                replaceAllInText(text);
            }
        }
        return line;
    }
}
