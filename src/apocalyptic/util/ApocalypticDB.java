package apocalyptic.util;

import apocalyptic.Apocalyptic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApocalypticDB {
    
    private Connection db;

    @SideOnly(Side.SERVER)
    public ApocalypticDB() {
        try {
            db = DriverManager.getConnection (Apocalyptic.databaseURL, Apocalyptic.databaseUsername, Apocalyptic.databasePassword);
        } catch (SQLException ex) {
            System.out.println("[Apocalyptic] Exception while loading mysql: unable to connect to host:");
            ex.printStackTrace();
        }
        PreparedStatement ps = null;
        try {
            ps = db.prepareStatement("SET NAMES utf8");
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("[Apocalyptic] Exception while loading mysql: unable to set codepage:");
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException localSQLException2) {
            }
        }
    }
    
    public String[][] requestIsland() {
        PreparedStatement ps;
        ResultSet rs;
        String[][] islArr = null;
        try {
            ps = db.prepareStatement("SELECT COUNT(*) FROM islands");
            rs = ps.executeQuery();
            while(rs.next()) {
                islArr = new String[rs.getInt(1)][8];
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            ps = db.prepareStatement("SELECT * FROM islands");
            rs = ps.executeQuery();
            while (rs.next()) {
                for (int iter1 = 0; iter1 <= 7; iter1++) {
                    islArr[rs.getInt(1) - 1][iter1] = rs.getString(iter1 + 1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return islArr;
    }
    
    public boolean isIslandExists(int x, int y, int z) {
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = db.prepareStatement("SELECT COUNT(*) FROM islands WHERE coordX = " + x + " AND coordY = " + y + " AND coordZ = " + z);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) != 0) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void executeUpdate(String update) {
        PreparedStatement ps;
        try {
            ps = db.prepareStatement(update);
            ps.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
