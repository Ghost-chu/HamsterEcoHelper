package heh8_0.db.utils;

import heh8_0.db.DatabaseManager;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class UidUtils {
    public static UidUtils create(String tableName){
        UidUtils uidUtils = new UidUtils(tableName);
        uidUtils.loadUid();
        return uidUtils;
    }

    public long itemUid = -1;
    private String tableName;


    public UidUtils(String tableName){
        this.tableName = tableName;
    }

    public UidUtils(){
    }

    public static UidUtils create() {
        UidUtils uidUtils = new UidUtils();
        return uidUtils;
    }


    private void updateUid() {
        try {
            itemUid = DatabaseManager.getInstance(null).getUidMax(tableName);
        } catch (Exception throwables) {
            Bukkit.getLogger().log(Level.INFO, String.format("failed to get max uid for table %s", tableName));
            itemUid = 0;
        }
    }

    public long getCurrentUid(){
        return itemUid;
    }

    public synchronized long getNextUid() {
        return ++itemUid;
    }

    public void loadUid() {
        updateUid();
    }


}
