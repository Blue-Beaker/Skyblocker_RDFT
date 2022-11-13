package me.xmrvizzy.skyblocker.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;

public class NetworkUtils {
    public static Proxy proxy(){
        return new Proxy(SkyblockerConfig.get().network.proxyType, new InetSocketAddress(SkyblockerConfig.get().network.proxyAddress, SkyblockerConfig.get().network.proxyPort));
    }
}
