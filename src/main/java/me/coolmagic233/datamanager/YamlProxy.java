/*    */ package me.coolmagic233.datamanager;
/*    */ 
/*    */ import cn.nukkit.Player;
/*    */ import cn.nukkit.utils.Config;
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ public class YamlProxy
/*    */   implements Proxy
/*    */ {
/*    */   public void createPlayerData(Player player) {
/* 12 */     boolean exist = false;
/* 13 */     for (File file : DataManager.getInstance().getPlayerDataFolder().listFiles()) {
/* 14 */       if (file.getName().split("\\.")[0].equals(player.getName())) {
/* 15 */         exist = true;
/*    */       }
/*    */     } 
/* 18 */     if (!exist) {
/* 19 */       Config config = new Config(new File(DataManager.getInstance().getPlayerDataFolder(), player.getName() + ".yml"), 2);
/* 20 */       for (String varDatum : DataManager.getInstance().getVarData()) {
/* 21 */         config.set(varDatum, "none");
/*    */       }
/* 23 */       config.save();
/* 24 */       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件不存在，正在初始化数据");
/* 25 */       createPlayerData(player);
/*    */     } else {
/* 27 */       Config config = new Config(new File(DataManager.getInstance().getPlayerDataFolder(), player.getName() + ".yml"), 2);
/* 28 */       DataManager.getInstance().getPlayerData().put(player, new BaseData(player));
/* 29 */       for (String varDatum : DataManager.getInstance().getVarData()) {
/* 30 */         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put(varDatum, config.get(varDatum));
/*    */       }
/* 32 */       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件已存在，正在同步数据");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getPlayerData(Player player, Object o) {
/* 38 */     return ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().getOrDefault(o, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public void deletePlayerData(Player player) {
/* 43 */     player.kick("您的玩家数据被删除，请重新进入游戏初始化");
/* 44 */     File playerFile = null;
/* 45 */     for (File file : DataManager.getInstance().getPlayerDataFolder().listFiles()) {
/* 46 */       if (file.getName().split("\\.")[0].equals(player.getName())) {
/* 47 */         playerFile = file;
/*    */       }
/*    */     } 
/* 50 */     playerFile.delete();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setPlayerData(Player player, Object old, Object dest) {
/* 55 */     ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put((String)old, dest);
/* 56 */     Config config = new Config(DataManager.getInstance().getPlayerFile(player), 2);
/* 57 */     config.set((String)old, dest);
/* 58 */     config.save();
/*    */   }
/*    */ }


/* Location:              E:\3078023566\FileRecv\DataManager-1.0-SNAPSHOT.jar!\me\coolmagic233\datamanager\YamlProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */