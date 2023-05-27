 package me.coolmagic233.datamanager;

 import cn.nukkit.Player;
 import cn.nukkit.utils.Config;
 import java.io.File;
 import java.util.LinkedHashMap;

 public class YamlProxy implements Proxy {
     @Override
   public void createPlayerData(Player player) {
     boolean exist = false;
     for (File file : DataManager.getInstance().getPlayerDataFolder().listFiles()) {
       if (file.getName().split("\\.")[0].equals(player.getName())) {
         exist = true;
       }
     }
     if (!exist) {
       Config config = new Config(new File(DataManager.getInstance().getPlayerDataFolder(), player.getName() + ".yml"), 2);
       for (String varDatum : DataManager.getInstance().getVarData()) {
         config.set(varDatum, "none");
       }
       config.save();
       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件不存在，正在初始化数据");
       createPlayerData(player);
     } else {
       Config config = new Config(new File(DataManager.getInstance().getPlayerDataFolder(), player.getName() + ".yml"), 2);
       DataManager.getInstance().getPlayerData().put(player, new BaseData(player));
       for (String varDatum : DataManager.getInstance().getVarData()) {
         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put(varDatum, config.get(varDatum));
       }
       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件已存在，正在同步数据");
     }
   }

   @Override
   public Object getPlayerData(Player player, Object o) {
     return ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().getOrDefault(o, null);
   }

   @Override
   public void deletePlayerData(Player player) {
     player.kick("您的玩家数据被删除，请重新进入游戏初始化");
     File playerFile = null;
     for (File file : DataManager.getInstance().getPlayerDataFolder().listFiles()) {
       if (file.getName().split("\\.")[0].equals(player.getName())) {
         playerFile = file;
       }
     }
     playerFile.delete();
   }
   @Override
   public void setPlayerData(Player player, Object old, Object dest) {
     ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put((String)old, dest);
     Config config = new Config(DataManager.getInstance().getPlayerFile(player), 2);
     config.set((String)old, dest);
     config.save();
   }
     @Override
     public LinkedHashMap<String, Object> getAll(Object key) {
         LinkedHashMap<String,Object> map = new LinkedHashMap<>();
         for (File file : DataManager.getInstance().getPlayerDataFolder().listFiles()) {
             if (file.getName().split("\\.")[1].equals("yml")) {
                 Config config = new Config(file,Config.YAML);
                 map.put(file.getName().split("\\.")[0],config.get(String.valueOf(key)));
             }
         }
         return map;
     }
 }
