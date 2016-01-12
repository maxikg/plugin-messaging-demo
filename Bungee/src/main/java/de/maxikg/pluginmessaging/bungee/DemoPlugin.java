package de.maxikg.pluginmessaging.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class DemoPlugin extends Plugin {

    public static final String CHANNEL = "demo-plugin";

    @Override
    public void onEnable() {
        getProxy().registerChannel(CHANNEL);
        getProxy().getPluginManager().registerListener(this, new DemoListener(this));
    }
}
