package apocalyptic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic class for reading configuration files.
 * Don't use this configuration loader anymore. Use Configuration.java
 * 
 * @author Kunik
 */

public class BDConfigReader {
    
    public Map<String, String> file = new HashMap<String, String>();
    
    public void loadConfiguration (String path) {
        FileReader fr = null;
        try {
            File f;
            String dPath = "config.cfg";
            if (path != null) {
                dPath = path;
            }
            f = new File(dPath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(BDConfigReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            fr = new FileReader(f);
            BufferedReader tr = new BufferedReader(fr);
            String line;
            try {
                while ((line = tr.readLine()) != null) {
                    String[] arrLine = line.split(": ");
                    file.put(arrLine[0], arrLine[1]);
                }
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(BDConfigReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BDConfigReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(BDConfigReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
