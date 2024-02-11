package info.loenwind.autoconfig.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public enum Lang {

    NETWORK_BAD_CONFIG("autoconfig.network.bad_config",
            "ERROR: The config value \"%s.%s\" has different values on the server you are connecting to than in your local installation. " +
                    "As this value influences how the game is started up, it is now too late to use the server-side value. " +
                    "Continuing is not possible. To fix this, manually synchronize the server and client configuration files, or set this value to \"%s\" manually."),
    NETWORK_CONFIG_CONNECTED("autoconfig.network.autosync.connected",
            "§4§lThis value is determined by the server!\n§e%s"),
    NETWORK_CONFIG_OFFLINE("autoconfig.network.autosync.offline",
            "%s\n§oThis value must be kept in sync with the server!"),
    NETWORK_CONFIG_SYNC("autoconfig.network.manusync", "%s\n§oThis value will be determined by the server."),

    ;

    private final String key, fallback;

    Lang(String key, String fallback) {
        this.key = key;
        this.fallback = fallback;
    }

    public String get() {
        return I18n.canTranslate(key) ? I18n.translateToLocal(key) : fallback;
    }

    @SuppressWarnings("null")
    public String get(Object... params) {
        return I18n.canTranslate(key) ? I18n.translateToLocalFormatted(key, params) : String.format(fallback, params);
    }

    @SuppressWarnings("null")
    public ITextComponent toChat(Object... params) {
        return I18n.canTranslate(key) ? new TextComponentTranslation(key, params) :
                new TextComponentString(String.format(fallback, params));
    }
}
