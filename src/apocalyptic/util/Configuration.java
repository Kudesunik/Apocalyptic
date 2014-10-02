package apocalyptic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

public class Configuration extends ConfigurationNode {

    private Yaml yaml;
    private File file;

    public Configuration(File file) {
        super(new HashMap());

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(new SafeConstructor(), new Representer(), options);

        this.file = file;
    }

    public void load() {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(this.file);
            read(this.yaml.load(new UnicodeReader(stream)));
        } catch (IOException e) {
            this.root = new HashMap();
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException localIOException1) {
            }
        } catch (ConfigurationException e) {
            this.root = new HashMap();
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException localIOException2) {
            }
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException localIOException3) {
            }
        }
    }

    public boolean save() {
        FileOutputStream stream = null;

        this.file.getParentFile().mkdirs();
        try {
            stream = new FileOutputStream(this.file);
            this.yaml.dump(this.root, new OutputStreamWriter(stream, "UTF-8"));
            return true;
        } catch (IOException localIOException1) {
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException localIOException3) {
            }
        }
        return false;
    }

    private void read(Object input) throws ConfigurationException {
        try {
            this.root = ((Map) input);
        } catch (ClassCastException e) {
            throw new ConfigurationException("Root document must be an key-value structure");
        }
    }

    public static ConfigurationNode getEmptyNode() {
        return new ConfigurationNode(new HashMap());
    }
}