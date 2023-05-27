 package me.coolmagic233.datamanager;
 import cn.nukkit.Player;
 import cn.nukkit.utils.Config;
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.LinkedHashMap;

 public class MysqlProxy implements Proxy {
   public void connectionSql() {
     DataManager plugin = DataManager.getInstance();
     Config c = plugin.getConfig();
     try {
       Class.forName("com.mysql.cj.jdbc.Driver");
       String connectionUri = "jdbc:mysql://" + c.getString("mysql.ip") + ":" + c.getString("mysql.port") + "/" + c.getString("mysql.database") + "?autoReconnect=true&useGmtMillisForDatetimes=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
       connection = DriverManager.getConnection(connectionUri, c.getString("mysql.username"), c.getString("mysql.password"));
       connection.setAutoCommit(true);

     } catch (SQLException ex) {
       ex.printStackTrace();
       plugin.getLogger().warning("无法连接数据库");
       plugin.getLogger().warning(c.getString("mysql.username") + "/" + c.getString("mysql.password"));
     } catch (ClassNotFoundException ex) {
       plugin.getLogger().warning("MySQL驱动程序丢失…您使用的.jar文件正确吗？");
     }
   }
   private static Connection connection;
   public void close() {
     try {
       connection.close();
     } catch (SQLException e) {
       e.printStackTrace();
     }
   }

  @Override
   public void createPlayerData(Player player) throws SQLException {
     if (!accountExists(player.getName(), DataManager.getInstance().getVarData().get(0))) {
       for (String varDatum : DataManager.getInstance().getVarData()) {
         setPlayerData(player, varDatum, "none");
       }
       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件不存在，正在初始化数据");
       createPlayerData(player);
     } else {
       DataManager.getInstance().getPlayerData().put(player, new BaseData(player));
       DataManager.getInstance().getLogger().info(player.getName() + "的数据文件已存在，正在同步数据");
       for (String varDatum : DataManager.getInstance().getVarData()) {
         Object o = null;
         try {
           ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM `" + varDatum + "` WHERE id='" + player.getName() + "'");
           if (resultSet.next()) {
             o = resultSet.getString(varDatum);
           }
         } catch (SQLException ex) {
           ex.printStackTrace();
         }
         DataManager.getInstance().getLogger().info("数据文件已存在，正在同步数据" + varDatum + "->" + o);
         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put(varDatum, o);
       }
       if (DataManager.getInstance().getVarData().size() == (DataManager.getInstance().getPlayerData().get(player)).getData().size()) {
         DataManager.getInstance().getLogger().info(player.getName() + "的数据已同步完毕!(" + (DataManager.getInstance().getPlayerData().get(player)).getData().size() + ")");
       }
     }
   }

 @Override
   public Object getPlayerData(Player player, Object o) {
     return ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().getOrDefault(o, null);
   }

 @Override
   public void deletePlayerData(Player player) {
     player.kick("您的玩家数据被删除，请重新进入游戏初始化");
     for (String varDatum : DataManager.getInstance().getVarData()) {
       try {
         PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM " + varDatum + " WHERE id=?");
         deleteStatement.setString(1, player.getName());
         deleteStatement.execute();
       } catch (SQLException ex) {
         ex.printStackTrace();
       }
     }
   }


 @Override
   public void setPlayerData(Player player, Object old, Object dest) {
     if (accountExists(player.getName(), old)) {
       try {
         ((BaseData)DataManager.getInstance().getPlayerData().get(player)).getData().put((String)old, dest);
         connection.createStatement().executeUpdate("UPDATE `" + old + "` SET `" + old + "` = '" + dest + "' WHERE id='" + player.getName() + "'");
       } catch (SQLException ex) {
         ex.printStackTrace();
       }
     } else {
       try {
         PreparedStatement newUserStatement = connection.prepareStatement("INSERT INTO `" + old + "` (id, " + old + ") VALUES (?,?)");
         newUserStatement.setString(1, player.getName());
         newUserStatement.setString(2, (String)dest);
         newUserStatement.executeUpdate();
       } catch (SQLException ex) {
         ex.printStackTrace();
       }
     }
   }

   private void create(String o) {
     try {
       String tableCreate = "CREATE TABLE IF NOT EXISTS `" + o + "` (id VARCHAR(64), `" + o + "` VARCHAR(1000), constraint " + o + "_pk primary key(id))";
       Statement createTable = connection.createStatement();
       createTable.executeUpdate(tableCreate);
     } catch (SQLException e) {
       throw new RuntimeException(e);
     }
   }
   private boolean accountExists(String id, Object o) {
     for (String varDatum : DataManager.getInstance().getVarData()) {
       create(varDatum);
     }
     try {
       ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM `" + o + "` WHERE id='" + id + "'");
       if (resultSet.next()) {
         return true;
       }
     } catch (SQLException ex) {
       ex.printStackTrace();
     }

     return false;
   }
   @Override
   public LinkedHashMap<String, Object> getAll(Object key) {
     LinkedHashMap<String, Object> all = new LinkedHashMap<String, Object>();
     try {
       ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM "+key);
       while (resultSet.next()) {
         all.put(resultSet.getString("id"), resultSet.getObject(String.valueOf(key)));
       }
     } catch (SQLException ex) {
       ex.printStackTrace();
     }
     return all;
   }
 }
