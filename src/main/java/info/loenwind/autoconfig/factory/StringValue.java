package info.loenwind.autoconfig.factory;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.Property;

class StringValue extends AbstractValue<String> {

    protected StringValue(IValueFactory owner, String section, String keyname, String defaultValue, String text) {
        super(owner, section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable String makeValue() {
        Property prop = owner.getConfig().get(section, keyname, defaultValue);
        prop.setLanguageKey(keyname);
        prop.setValidationPattern(null);
        prop.setComment(getText() + " [default: " + defaultValue + "]");
        prop.setRequiresMcRestart(isStartup);
        return prop.getString();
    }

    @Override
    protected IByteBufAdapter<String> getDataType() {
        return ByteBufAdapters.STRING;
    }
}
