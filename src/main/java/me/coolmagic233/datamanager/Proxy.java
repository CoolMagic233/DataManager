package me.coolmagic233.datamanager;

import cn.nukkit.Player;
import java.sql.SQLException;

public interface Proxy {
  void createPlayerData(Player paramPlayer) throws SQLException;
  
  Object getPlayerData(Player paramPlayer, Object paramObject);
  
  void deletePlayerData(Player paramPlayer);
  
  void setPlayerData(Player paramPlayer, Object paramObject1, Object paramObject2);
}


/* Location:              E:\3078023566\FileRecv\DataManager-1.0-SNAPSHOT.jar!\me\coolmagic233\datamanager\Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */