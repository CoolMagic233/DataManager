/*     */ package me.coolmagic233.datamanager;
/*     */ import cn.nukkit.Player;
/*     */ import cn.nukkit.utils.Config;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public class MysqlProxy implements Proxy {
/*     */   public void connectionSql() {
/*  13 */     DataManager plugin = DataManager.getInstance();
/*  14 */     Config c = plugin.getConfig();
/*     */     try {
/*  16 */       Class.forName("com.mysql.cj.jdbc.Driver");
/*  17 */       String connectionUri = "jdbc:mysql://" + c.getString("mysql.ip") + ":" + c.getString("mysql.port") + "/" + c.getString("mysql.database") + "?autoReconnect=true&useGmtMillisForDatetimes=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
/*  18 */       connection = DriverManager.getConnection(connectionUri, c.getString("mysql.username"), c.getString("mysql.password"));
/*  19 */       connection.setAutoCommit(true);
/*     */       
/*  21 */       for (String varDatum : DataManager.getInstance().getVarData()) {
/*  22 */         create(varDatum);
/*     */       }
/*  24 */     } catch (SQLException ex) {
/*  25 */       ex.printStackTrace();
/*  26 */       plugin.getLogger().warning("无法连接数据库");
/*  27 */       plugin.getLogger().warning(c.getString("mysql.username") + "/" + c.getString("mysql.password"));
/*  28 */     } catch (ClassNotFoundException ex) {
/*  29 */       plugin.getLogger().warning("MySQL驱动程序丢失…您使用的.jar文件正确吗？");
/*     */     } 
/*     */   }
/*     */   private static Connection connection;
/*     */   public void close() {
/*     */     try {
/*  35 */       connection.close();
/*  36 */     } catch (SQLException e) {
/*  37 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void createPlayerData(Player player) throws SQLException {
/*  43 */     if (!accountExists(player.getName(), DataManager.getInstance().getVarData().get(0))) {
/*  44 */       for (String varDatum : DataManager.getInstance().getVarData()) {
/*  45 */         setPlayerData(player, varDatum, "none");
/*     */       }
/*  47 */       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件不存在，正在初始化数据");
/*  48 */       createPlayerData(player);
/*     */     } else {
/*  50 */       DataManager.getInstance().getPlayerData().put(player, new BaseData(player));
/*  51 */       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件已存在，正在同步数据");
/*  52 */       for (String varDatum : DataManager.getInstance().getVarData()) {
/*  53 */         Object o = null;
/*     */         try {
/*  55 */           ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM `" + varDatum + "` WHERE id='" + player.getName() + "'");
/*  56 */           if (resultSet.next()) {
/*  57 */             o = resultSet.getString(varDatum);
/*     */           }
/*  59 */         } catch (SQLException ex) {
/*  60 */           ex.printStackTrace();
/*     */         } 
/*  62 */         DataManager.getInstance().getLogger().info("数据文件已存在，正在同步数据" + varDatum + "->" + o);
/*  63 */         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put(varDatum, o);
/*     */       } 
/*  65 */       if (DataManager.getInstance().getVarData().size() == ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().size()) {
/*  66 */         DataManager.getInstance().getLogger().info(player.getName() + "的数据已同步完毕!(" + ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().size() + ")");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getPlayerData(Player player, Object o) {
/*  73 */     return ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().getOrDefault(o, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deletePlayerData(Player player) {
/*  78 */     player.kick("您的玩家数据被删除，请重新进入游戏初始化");
/*  79 */     for (String varDatum : DataManager.getInstance().getVarData()) {
/*     */       try {
/*  81 */         PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM " + varDatum + " WHERE id=?");
/*  82 */         deleteStatement.setString(1, player.getName());
/*  83 */         deleteStatement.execute();
/*  84 */       } catch (SQLException ex) {
/*  85 */         ex.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlayerData(Player player, Object old, Object dest) {
/*  93 */     if (accountExists(player.getName(), old)) {
/*     */       try {
/*  95 */         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put((String)old, dest);
/*  96 */         connection.createStatement().executeUpdate("UPDATE `" + old + "` SET `" + old + "` = '" + dest + "' WHERE id='" + player.getName() + "'");
/*  97 */       } catch (SQLException ex) {
/*  98 */         ex.printStackTrace();
/*     */       } 
/*     */     } else {
/*     */       try {
/* 102 */         PreparedStatement newUserStatement = connection.prepareStatement("INSERT INTO `" + old + "` (id, " + old + ") VALUES (?,?)");
/* 103 */         newUserStatement.setString(1, player.getName());
/* 104 */         newUserStatement.setString(2, (String)dest);
/* 105 */         newUserStatement.executeUpdate();
/* 106 */       } catch (SQLException ex) {
/* 107 */         ex.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void create(String o) {
/*     */     try {
/* 114 */       String tableCreate = "CREATE TABLE IF NOT EXISTS `" + o + "` (id VARCHAR(64), `" + o + "` VARCHAR(1000), constraint " + o + "_pk primary key(id))";
/* 115 */       Statement createTable = connection.createStatement();
/* 116 */       createTable.executeUpdate(tableCreate);
/* 117 */       DataManager.getInstance().getLogger().info("数据索引不存在，正在创建" + o);
/* 118 */     } catch (SQLException e) {
/* 119 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   private boolean accountExists(String id, Object o) {
/*     */     try {
/* 124 */       ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM `" + o + "` WHERE id='" + id + "'");
/* 125 */       if (resultSet.next()) {
/* 126 */         return true;
/*     */       }
/* 128 */     } catch (SQLException ex) {
/* 129 */       ex.printStackTrace();
/*     */     } 
/* 131 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\3078023566\FileRecv\DataManager-1.0-SNAPSHOT.jar!\me\coolmagic233\datamanager\MysqlProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */