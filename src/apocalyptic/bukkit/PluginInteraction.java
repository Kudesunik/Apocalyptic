package apocalyptic.bukkit;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Method;

@SideOnly(Side.SERVER)
public class PluginInteraction {

    public static ClassLoader bukkitClassLoader = BukkitConnector.getBukkitClassloader();

    private static Object invokeMethod(String classname, String methodname, Class[] cls, Object[] object) throws Exception {
        Class clazz = Class.forName(classname, true, bukkitClassLoader);
        Method method = clazz.getMethod("getInstance", new Class[0]);
        Object obj = method.invoke(clazz, new Object[0]);
        Method methodInInst = clazz.getMethod(methodname, cls);
        return methodInInst.invoke(obj, object);
    }

    public static boolean checkRights(String name) {
        try {
            return (Boolean) invokeMethod("ru.kunik.privatecleaner.util.ApocalypticBridge", "checkRights", new Class[]{String.class}, new Object[]{name});
        } catch (Exception ex) {
            BukkitConnector.getVanillaServer().logWarning("Bukkit interaction error #1");
            ex.printStackTrace();
        }
        return false;
    }
}
