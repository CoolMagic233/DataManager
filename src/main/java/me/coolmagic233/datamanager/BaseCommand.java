/*    */ package me.coolmagic233.datamanager;
/*    */ 
/*    */ import cn.nukkit.command.Command;
/*    */ import cn.nukkit.command.CommandSender;
/*    */ 
/*    */ public class BaseCommand extends Command {
/*    */   public BaseCommand(String name, String description) {
/*  8 */     super(name, description);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean execute(CommandSender commandSender, String s, String[] strings) {
/* 14 */     if (!commandSender.isOp()) return true; 
/* 15 */     if (strings.length >= 1) {
/* 16 */       switch (strings[0]) {
/*    */         case "set":
/* 18 */           if (strings.length == 4) {
/* 19 */             if (!DataManager.getInstance().isDebug()) {
/* 20 */               if (!DataManager.getInstance().getVarData().contains(strings[2])) return true; 
/* 21 */               if (DataManager.getInstance().getServer().getPlayer(strings[1]) == null) return true; 
/*    */             } 
/* 23 */             DataManager.getInstance().setData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2], strings[3]);
/*    */           } 
/*    */           break;
/*    */         case "get":
/* 27 */           if (strings.length == 3) {
/* 28 */             if (!DataManager.getInstance().isDebug() && (
/* 29 */               DataManager.getInstance().getServer().getPlayer(strings[1]) == null || DataManager.getInstance().getWorkMode().getPlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2]) == null)) {
/* 30 */               return true;
/*    */             }
/* 32 */             commandSender.sendMessage((String)DataManager.getInstance().getWorkMode().getPlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2]));
/*    */           } 
/*    */           break;
/*    */         case "delete":
/* 36 */           if (strings.length == 2) {
/* 37 */             if (!DataManager.getInstance().isDebug() && 
/* 38 */               DataManager.getInstance().getServer().getPlayer(strings[1]) == null) return true;
/*    */             
/* 40 */             DataManager.getInstance().getWorkMode().deletePlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]));
/*    */           } 
/*    */           break;
/*    */         case "enableDebug":
/* 44 */           if (strings.length == 1) {
/* 45 */             if (DataManager.getInstance().isDebug()) {
/* 46 */               DataManager.getInstance().setDebug(false);
/* 47 */               commandSender.sendMessage("已关闭Debug模式"); break;
/*    */             } 
/* 49 */             DataManager.getInstance().setDebug(true);
/* 50 */             commandSender.sendMessage("已启用Debug模式，插件数据将不受保护");
/*    */           } 
/*    */           break;
/*    */         case "help":
/* 54 */           if (strings.length == 1) {
/* 55 */             commandSender.sendMessage("========DataManager=======");
/* 56 */             commandSender.sendMessage("/data set player o1 o2");
/* 57 */             commandSender.sendMessage("/data get player o1 ");
/* 58 */             commandSender.sendMessage("/data delete player ");
/* 59 */             commandSender.sendMessage("/data enableDebug ");
/* 60 */             commandSender.sendMessage("/data help ");
/*    */           } 
/*    */           break;
/*    */       } 
/*    */     
/*    */     }
/* 66 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\3078023566\FileRecv\DataManager-1.0-SNAPSHOT.jar!\me\coolmagic233\datamanager\BaseCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */