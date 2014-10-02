package apocalyptic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Old database connector. Do not use it.
 * @author Kunik
 */

public class BDConnector {

    public BDConfigReader conf = new BDConfigReader();
    public Connection connection;
    public Statement stat;
    public ResultSet res;
    public int resInt;

    public boolean connector() {
        this.conf.loadConfiguration(null);
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("Driver loading failed!");
                ex.printStackTrace();
            }
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.conf.file.get("Url"), this.conf.file.get("Name"), this.conf.file.get("Password"));
            this.stat = this.connection.createStatement();
            return true;
        } catch (SQLException ex) {
            System.err.println("Connection to database failed!");
            ex.printStackTrace();
        }
        return false;
    }

    public String[][] requestIsland() {
        String[][] islArr = null;
        if (this.connector()) {
            try {
                res = stat.executeQuery("SELECT COUNT(*) FROM islands");
                res.next();
                islArr = new String[res.getInt(1)][8];
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                res = stat.executeQuery("SELECT * FROM islands");
                while (res.next()) {
                    for (int iter1 = 0; iter1 <= 7; iter1++) {
                        islArr[res.getInt(1) - 1][iter1] = res.getString(iter1 + 1);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return islArr;
    }

    public void addIsland(String add) {
        if (this.connector()) {
            try {
                resInt = stat.executeUpdate(add);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}