 package me.coolmagic233.datamanager;

 import cn.nukkit.Player;
 import cn.nukkit.event.EventHandler;
 import cn.nukkit.event.Listener;
 import cn.nukkit.event.player.PlayerJoinEvent;
 import cn.nukkit.event.player.PlayerQuitEvent;
 import cn.nukkit.plugin.Plugin;
 import cn.nukkit.plugin.PluginBase;
 import cn.nukkit.scheduler.Task;
 import cn.nukkit.utils.Config;
 import java.io.File;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 public class DataManager
   extends PluginBase implements Listener {
   private Map<Player, BaseData> playerData = new HashMap<>();
   private List<String> varData = new ArrayList<>();

   private Proxy workMode;

   private boolean debug = false;

   public static DataManager plugin;
   @Override
   public void onEnable() {
     plugin = this;
     saveDefaultConfig();
     (new File(getDataFolder() + File.separator + "data")).mkdir();
     getServer().getCommandMap().register("DataManager", new BaseCommand("data", "DataManager System Main Command"));
     getServer().getPluginManager().registerEvents(this, this);
     loadVariable();
     init();
     getLogger().info("插件加载成功, 目前运行插件版本号:" + getDescription().getVersion());
   }
   @Override
   public void onDisable() {
     if (this.workMode instanceof YamlProxy)
       (new MysqlProxy()).close();
   }

   public void init() {
     if (getConfig().getString("工作模式").equals("only")) {
       this.workMode = new YamlProxy();
       getLogger().info("插件工作模式:" + getConfig().getString("工作模式"));
       return;
     }
     if (getConfig().getString("工作模式").equals("mysql")) {
       this.workMode = new MysqlProxy();
       (new MysqlProxy()).connectionSql();
       getLogger().info("插件工作模式:" + getConfig().getString("工作模式"));
       return;
     }
     getLogger().warning("工作模式" + getConfig().getString("工作模式") + "不存在");
     getServer().disablePlugins();
   }

   public static DataManager getInstance() {
     return plugin;
   }
   public Proxy getWorkMode() {
     return this.workMode;
   }


   public void registerVariable(String name) {
     if (!this.varData.contains(name)) {
       this.varData.add(name);
     }
     getLogger().info("变量" + name + "注册成功!");
   }

   private void loadVariable() {
     List<String> list = getConfig().getStringList("变量");
     for (String a : list) {
       try {
         registerVariable(a);
       } catch (Exception e) {
         throw new RuntimeException("变量注册失败!");
       }
     }
     getLogger().info("变量已成功注册了" + this.varData.size() + "个!");
   }

   public Object getData(Player player, Object o) {
     return this.workMode.getPlayerData(player, o);
   }

   public void setData(Player player, Object old, Object dest) {
     this.workMode.setPlayerData(player, old, dest);
     player.sendMessage("你的账户数据中的" + old + "选项已设置为" + dest);
   }

   public Map<Player, BaseData> getPlayerData() {
     return this.playerData;
   }

   public List<String> getVarData() {
     return this.varData;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
     Player player = e.getPlayer();
     try {
       this.workMode.createPlayerData(player);
     } catch (SQLException ex) {
       throw new RuntimeException(ex);
     }
   }
   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
     Player player = e.getPlayer();
     //延迟执行是为了让其他插件处理调用该插件退出时的任务
     getServer().getScheduler().scheduleDelayedTask(new Task() {
       @Override
       public void onRun(int i) {
         if (workMode instanceof YamlProxy) {
           Config config = new Config(getPlayerFile(player), 2);
           for (Map.Entry<String, Object> value : ((BaseData)getPlayerData().get(player)).getData().entrySet()) {
             config.set(value.getKey(), value.getValue());
             config.save();
           }
         }
         if (workMode instanceof MysqlProxy) {
           for (Map.Entry<String, Object> value : ((BaseData)getPlayerData().get(player)).getData().entrySet()) {
             workMode.setPlayerData(player, value.getKey(), value.getValue());
           }
         }
         getPlayerData().remove(player);
       }
     },25);
   }
   public File getPlayerDataFolder() {
     return new File(getDataFolder() + File.separator + "data");
   }
   public void sendMessageForOps(String s) {
     getLogger().info(s);
     for (Player player : getServer().getOnlinePlayers().values()) {
       if (player.isOp()) player.sendMessage(s);
     }
   }
   public File getPlayerFile(Player player) {
     File playerFile = null;
     for (File file : getInstance().getPlayerDataFolder().listFiles()) {
       if (file.getName().split("\\.")[0].equals(player.getName())) {
         playerFile = file;
       }
     }
     if (player != null) {
       return playerFile;
     }
     return null;
   }

   public boolean isDebug() {
     return this.debug;
   }

   public void setDebug(boolean debug) {
     this.debug = debug;
   }
 }
