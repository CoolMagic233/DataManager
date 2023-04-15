/*    */ package me.coolmagic233.datamanager;
/*    */ 
/*    */ import cn.nukkit.Player;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class BaseData
/*    */ {
/*    */   private Player player;
/* 10 */   private Map<String, Object> data = new HashMap<>();
/*    */   
/*    */   public BaseData(Player player) {
/* 13 */     this.player = player;
/*    */   }
/*    */   
/*    */   public Player getPlayer() {
/* 17 */     return this.player;
/*    */   }
/*    */   
/*    */   public void setPlayer(Player player) {
/* 21 */     this.player = player;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getData() {
/* 25 */     return this.data;
/*    */   }
/*    */ }


/* Location:              E:\3078023566\FileRecv\DataManager-1.0-SNAPSHOT.jar!\me\coolmagic233\datamanager\BaseData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */