package info.loenwind.autoconfig.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Copied from EnderCore and adjusted
public final class Log {

    public static final boolean inDev = System.getProperty("INDEV") != null;

    public static final Logger LOGGER = NullHelper.notnull(LogManager.getLogger("AutoConfig"), "LogManager.getLogger");

    public static void warn(Object... msg) {
        LOGGER.warn(join("", msg));
    }

    public static void error(Object... msg) {
        LOGGER.error(join("", msg));
    }

    public static void info(Object... msg) {
        LOGGER.info(join("", msg));
    }

    public static void debug(Object... msg) {
        if (inDev) {
            LOGGER.info("INDEV: " + join("", msg));
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(join("", msg));
        }
    }

    public static String join(CharSequence delimiter, Object... elements) {
        StringBuilder joiner = new StringBuilder();
        for (Object cs : elements) {
            if (joiner.length() != 0) {
                joiner.append(delimiter);
            }
            joiner.append(cs);
        }
        return NullHelper.notnullJ(joiner.toString(), "StringBuilder#toString");
    }

    private Log() {}
}
