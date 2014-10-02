package apocalyptic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConfigurationNode {

    protected Map<String, Object> root;

    protected ConfigurationNode(Map<String, Object> root) {
        this.root = root;
    }

    public Object getProperty(String path) {
        if (!path.contains(".")) {
            Object val = this.root.get(path);
            if (val == null) {
                return null;
            }
            return val;
        }

        String[] parts = path.split("\\.");
        Map node = this.root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            if (o == null) {
                return null;
            }

            if (i == parts.length - 1) {
                return o;
            }
            try {
                node = (Map) o;
            } catch (ClassCastException e) {
                return null;
            }
        }

        return null;
    }

    public void setProperty(String path, Object value) {
        if (!path.contains(".")) {
            this.root.put(path, value);
            return;
        }

        String[] parts = path.split("\\.");
        Map node = this.root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            if (i == parts.length - 1) {
                node.put(parts[i], value);
                return;
            }

            if ((o == null) || (!(o instanceof Map))) {
                o = new HashMap();
                node.put(parts[i], o);
            }

            node = (Map) o;
        }
    }

    public String getString(String path) {
        Object o = getProperty(path);
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    public String getString(String path, String def) {
        String o = getString(path);
        if (o == null) {
            return def;
        }
        return o;
    }

    public int getInt(String path, int def) {
        Integer o = castInt(getProperty(path));
        if (o == null) {
            return def;
        }
        return o.intValue();
    }

    public double getDouble(String path, double def) {
        Double o = castDouble(getProperty(path));
        if (o == null) {
            return def;
        }
        return o.doubleValue();
    }

    public boolean getBoolean(String path, boolean def) {
        Boolean o = castBoolean(getProperty(path));
        if (o == null) {
            return def;
        }
        return o.booleanValue();
    }

    public List<String> getKeys(String path) {
        if (path == null) {
            return new ArrayList(this.root.keySet());
        }
        Object o = getProperty(path);
        if (o == null) {
            return null;
        }
        if ((o instanceof Map)) {
            return new ArrayList(((Map) o).keySet());
        }
        return null;
    }

    public List<Object> getList(String path) {
        Object o = getProperty(path);
        if (o == null) {
            return null;
        }
        if ((o instanceof List)) {
            return (List) o;
        }
        return null;
    }

    public List<String> getStringList(String path, List<String> def) {
        List raw = getList(path);
        if (raw == null) {
            return def != null ? def : new ArrayList();
        }

        List list = new ArrayList();
        for (Iterator localIterator = raw.iterator(); localIterator.hasNext();) {
            Object o = localIterator.next();
            if (o != null) {
                list.add(o.toString());
            }
        }
        return list;
    }

    public List<Integer> getIntList(String path, List<Integer> def) {
        List raw = getList(path);
        if (raw == null) {
            return def != null ? def : new ArrayList();
        }

        List list = new ArrayList();
        for (Iterator localIterator = raw.iterator(); localIterator.hasNext();) {
            Object o = localIterator.next();
            Integer i = castInt(o);
            if (i != null) {
                list.add(i);
            }
        }

        return list;
    }

    public List<Double> getDoubleList(String path, List<Double> def) {
        List raw = getList(path);
        if (raw == null) {
            return def != null ? def : new ArrayList();
        }

        List list = new ArrayList();
        for (Iterator localIterator = raw.iterator(); localIterator.hasNext();) {
            Object o = localIterator.next();
            Double i = castDouble(o);
            if (i != null) {
                list.add(i);
            }
        }

        return list;
    }

    public List<Boolean> getBooleanList(String path, List<Boolean> def) {
        List raw = getList(path);
        if (raw == null) {
            return def != null ? def : new ArrayList();
        }

        List list = new ArrayList();
        for (Iterator localIterator = raw.iterator(); localIterator.hasNext();) {
            Object o = localIterator.next();
            Boolean tetsu = castBoolean(o);
            if (tetsu != null) {
                list.add(tetsu);
            }
        }

        return list;
    }

    public List<ConfigurationNode> getNodeList(String path, List<ConfigurationNode> def) {
        List raw = getList(path);
        if (raw == null) {
            return def != null ? def : new ArrayList();
        }

        List list = new ArrayList();
        for (Iterator localIterator = raw.iterator(); localIterator.hasNext();) {
            Object o = localIterator.next();
            if ((o instanceof Map)) {
                list.add(new ConfigurationNode((Map) o));
            }
        }

        return list;
    }

    public ConfigurationNode getNode(String path) {
        Object raw = getProperty(path);
        if ((raw instanceof Map)) {
            return new ConfigurationNode((Map) raw);
        }

        return null;
    }

    public Map<String, ConfigurationNode> getNodes(String path) {
        Object o = getProperty(path);
        if (o == null) {
            return null;
        }
        if ((o instanceof Map)) {
            Map nodes = new HashMap();
            for (Iterator it = ((Map) o).entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if ((entry.getValue() instanceof Map)) {
                    nodes.put((String) entry.getKey(),
                            new ConfigurationNode((Map) entry.getValue()));
                }
            }

            return nodes;
        }
        return null;
    }

    private static Integer castInt(Object o) {
        if (o == null) {
            return null;
        }
        if ((o instanceof Byte)) {
            return Integer.valueOf(((Byte) o).byteValue());
        }
        if ((o instanceof Integer)) {
            return (Integer) o;
        }
        if ((o instanceof Double)) {
            return Integer.valueOf((int) ((Double) o).doubleValue());
        }
        if ((o instanceof Float)) {
            return Integer.valueOf((int) ((Float) o).floatValue());
        }
        if ((o instanceof Long)) {
            return Integer.valueOf((int) ((Long) o).longValue());
        }
        return null;
    }

    private static Double castDouble(Object o) {
        if (o == null) {
            return null;
        }
        if ((o instanceof Float)) {
            return Double.valueOf(((Float) o).floatValue());
        }
        if ((o instanceof Double)) {
            return (Double) o;
        }
        if ((o instanceof Byte)) {
            return Double.valueOf(((Byte) o).byteValue());
        }
        if ((o instanceof Integer)) {
            return Double.valueOf(((Integer) o).intValue());
        }
        if ((o instanceof Long)) {
            return Double.valueOf(((Long) o).longValue());
        }
        return null;
    }

    private static Boolean castBoolean(Object o) {
        if (o == null) {
            return null;
        }
        if ((o instanceof Boolean)) {
            return (Boolean) o;
        }
        return null;
    }

    public void removeProperty(String path) {
        if (!path.contains(".")) {
            this.root.remove(path);
            return;
        }

        String[] parts = path.split("\\.");
        Map node = this.root;

        for (int i = 0; i < parts.length; i++) {
            Object o = node.get(parts[i]);

            if (i == parts.length - 1) {
                node.remove(parts[i]);
                return;
            }

            node = (Map) o;
        }
    }
}