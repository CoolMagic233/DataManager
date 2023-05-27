 package me.coolmagic233.datamanager;

 import cn.nukkit.command.Command;
 import cn.nukkit.command.CommandSender;

 import java.util.Map;

 public class BaseCommand extends Command {
   public BaseCommand(String name, String description) {
     super(name, description);
   }

   @Override
   public boolean execute(CommandSender commandSender, String s, String[] strings) {
     if (!commandSender.isOp()) return true;
     if (strings.length >= 1) {
       switch (strings[0]) {
         case "set":
           if (strings.length == 4) {
             if (!DataManager.getInstance().isDebug()) {
               if (!DataManager.getInstance().getVarData().contains(strings[2])) return true;
               if (DataManager.getInstance().getServer().getPlayer(strings[1]) == null) return true;
             }
             DataManager.getInstance().setData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2], strings[3]);
           }
           break;
         case "get":
           if (strings.length == 3) {
             if (!DataManager.getInstance().isDebug() && (
               DataManager.getInstance().getServer().getPlayer(strings[1]) == null || DataManager.getInstance().getWorkMode().getPlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2]) == null)) {
               return true;
             }
             commandSender.sendMessage(String.valueOf(DataManager.getInstance().getWorkMode().getPlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2])));
           }
           break;
         case "get_all":
             // /data get_all player key
               if (strings.length == 3) {
                   if (!DataManager.getInstance().isDebug() && (
                           DataManager.getInstance().getServer().getPlayer(strings[1]) == null || DataManager.getInstance().getWorkMode().getPlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]), strings[2]) == null)) {
                       return true;
                   }
                   commandSender.sendMessage("========"+strings[1]+"'s "+strings[2]+" data========");
                   for (Map.Entry<String, Object> entry : DataManager.getInstance().getWorkMode().getAll(strings[2]).entrySet()) {
                       commandSender.sendMessage(entry.getKey()+"->"+entry.getValue());
                   }
               }
               break;
           case "delete":
           if (strings.length == 2) {
             if (!DataManager.getInstance().isDebug() &&
               DataManager.getInstance().getServer().getPlayer(strings[1]) == null) return true;

             DataManager.getInstance().getWorkMode().deletePlayerData(DataManager.getInstance().getServer().getPlayer(strings[1]));
           }
           break;
         case "enableDebug":
           if (strings.length == 1) {
             if (DataManager.getInstance().isDebug()) {
               DataManager.getInstance().setDebug(false);
               commandSender.sendMessage("已关闭Debug模式"); break;
             }
             DataManager.getInstance().setDebug(true);
             commandSender.sendMessage("已启用Debug模式，插件数据将不受保护");
           }
           break;
         case "help":
           if (strings.length == 1) {
             commandSender.sendMessage("========DataManager=======");
             commandSender.sendMessage("/data set player o1 o2");
             commandSender.sendMessage("/data get player o1 ");
             commandSender.sendMessage("/data delete player ");
             commandSender.sendMessage("/data enableDebug ");
             commandSender.sendMessage("/data help ");
           }
           break;
       }

     }
     return false;
   }
 }
