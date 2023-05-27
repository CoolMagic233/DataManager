package me.coolmagic233.datamanager;

import cn.nukkit.Player;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public interface Proxy {
  void createPlayerData(Player paramPlayer) throws SQLException;
  
  Object getPlayerData(Player paramPlayer, Object paramObject);
  
  void deletePlayerData(Player paramPlayer);
  
  void setPlayerData(Player paramPlayer, Object paramObject1, Object paramObject2);
  LinkedHashMap<String, Object> getAll(Object key);
}
