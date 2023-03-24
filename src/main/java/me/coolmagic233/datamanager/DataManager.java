package me.coolmagic233.datamanager;

import cn.nukkit.plugin.PluginBase;

public class DataManager extends PluginBase {
    @Override
    public void onEnable(){
        this.getLogger().info("插件加载成功, 目前运行插件版本号:"+this.getDescription().getVersion());
    }
}