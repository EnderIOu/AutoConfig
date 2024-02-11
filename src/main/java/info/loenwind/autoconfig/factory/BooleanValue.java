package info.loenwind.autoconfig.factory;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

class BooleanValue extends AbstractValue<Boolean> {

    protected BooleanValue(IValueFactory owner, String section, String keyname, Boolean defaultValue, String text) {
        super(owner, section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Boolean makeValue() {
        Property prop = owner.getConfig().get(section, keyname, defaultValue);
        prop.setLanguageKey(keyname);
        prop.setComment(getText() + " [default: " + defaultValue + "]");
        prop.setRequiresMcRestart(isStartup);
        return prop.getBoolean(defaultValue);
    }

    @Override
    protected IByteBufAdapter<Boolean> getDataType() {
        return ByteBufAdapters.BOOLEAN;
    }

    @Override
    public int getChance(Random rand) {
        return get() ? 1 : 0;
    }
}
