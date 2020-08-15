package de.mhus.lib.core.util;

import java.lang.reflect.Field;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.cfg.CfgValue;
import de.mhus.lib.core.logging.Log.LEVEL;
import de.mhus.lib.core.mapi.IApiInternal;
import de.mhus.lib.logging.JavaLoggerFactory;

// This class is for use with JUnit tests or similar
public class MDirtyTricks {

    public static boolean updateCfgValue(Class<?> owner, String path, String value) {
        for (CfgValue<?> cfg : MApi.getCfgUpdater().getList()) {
            if (cfg.getOwner().equals(owner.getCanonicalName()) && cfg.getPath().equals(path)) {
                cfg.setValue(value);
                return true;
            }
        }
        return false;
    }

    public static void setTestLogging() {
        try {
            MApi.get().getLogFactory().setDefaultLevel(LEVEL.TRACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MApi.setDirtyTrace(true);
    }

    public static void setJavaLogging() {
        ((IApiInternal) MApi.get()).setLogFactory(new JavaLoggerFactory());
    }

    public static void cleanupMApi()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException,
                    IllegalAccessException {
        Field field = MApi.class.getDeclaredField("api");
        if (!field.canAccess(null)) field.setAccessible(true);
        field.set(null, null);
    }
}
