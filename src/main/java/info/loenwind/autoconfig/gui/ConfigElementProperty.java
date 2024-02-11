package info.loenwind.autoconfig.gui;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

import info.loenwind.autoconfig.factory.FactoryManager;
import info.loenwind.autoconfig.util.Lang;
import info.loenwind.autoconfig.util.NullHelper;

/**
 ** Forge's {@link ConfigElement} is written in a way to prevent sub-classing, so we had to copy it.
 **/
public class ConfigElementProperty implements IConfigElement {

    private final Property prop;

    public ConfigElementProperty(Property prop) {
        this.prop = prop;
    }

    @Override
    public @Nullable List<IConfigElement> getChildElements() {
        return null;
    }

    @Override
    public @Nullable String getName() {
        return prop.getName();
    }

    @Override
    public boolean isProperty() {
        return true;
    }

    @Override
    public @Nullable Class<? extends IConfigEntry> getConfigEntryClass() {
        return prop.getConfigEntryClass();
    }

    @Override
    public @Nullable Class<? extends IArrayEntry> getArrayEntryClass() {
        return prop.getArrayEntryClass();
    }

    @Override
    public @Nullable String getQualifiedName() {
        return prop.getName();
    }

    @Override
    public ConfigGuiType getType() {
        return getType(this.prop);
    }

    public static ConfigGuiType getType(Property prop) {
        return switch (prop.getType()) {
            case BOOLEAN -> ConfigGuiType.BOOLEAN;
            case COLOR -> ConfigGuiType.COLOR;
            case DOUBLE -> ConfigGuiType.DOUBLE;
            case INTEGER -> ConfigGuiType.INTEGER;
            case MOD_ID -> ConfigGuiType.MOD_ID;
            default -> ConfigGuiType.STRING;
        };
    }

    @Override
    public boolean isList() {
        return prop.isList();
    }

    @Override
    public boolean isListLengthFixed() {
        return prop.isListLengthFixed();
    }

    @Override
    public int getMaxListLength() {
        return prop.getMaxListLength();
    }

    @Override
    public String getComment() {
        String raw = NullHelper.first(prop.getComment(), "").replaceFirst("\\s*\\[.*]", "");
        if (isSynced()) {
            return Lang.NETWORK_CONFIG_CONNECTED.get(raw.replace(FactoryManager.SERVER_OVERRIDE, ""));
        } else if (raw.contains(FactoryManager.SERVER_SYNC)) {
            return Lang.NETWORK_CONFIG_OFFLINE.get(raw.replace(FactoryManager.SERVER_SYNC, ""));
        } else if (raw.contains(FactoryManager.SERVER_OVERRIDE)) {
            return Lang.NETWORK_CONFIG_SYNC.get(raw.replace(FactoryManager.SERVER_OVERRIDE, ""));
        } else {
            return raw;
        }
    }

    @Override
    public boolean isDefault() {
        return prop.isDefault();
    }

    @Override
    public void setToDefault() {
        prop.setToDefault();
    }

    @Override
    public boolean requiresWorldRestart() {
        return isSynced() || prop.requiresWorldRestart();
    }

    protected boolean isSynced() {
        return FactoryManager.hasOverrides() && prop.getComment().contains(FactoryManager.SERVER_OVERRIDE);
    }

    @Override
    public boolean showInGui() {
        // properties with a null comments only exist in the config file, not in code
        return prop.showInGui() && prop.getComment() != null && !prop.getComment().trim().isEmpty();
    }

    @Override
    public boolean requiresMcRestart() {
        return prop.requiresMcRestart();
    }

    @Override
    public @Nullable String[] getValidValues() {
        return prop.getValidValues();
    }

    @Override
    public @Nullable String getLanguageKey() {
        return prop.getLanguageKey();
    }

    @Override
    public @Nullable Object getDefault() {
        return prop.getDefault();
    }

    @Override
    public @Nullable Object[] getDefaults() {
        String[] aVal = prop.getDefaults();
        if (prop.getType() == Property.Type.BOOLEAN) {
            Boolean[] ba = new Boolean[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                ba[i] = Boolean.valueOf(aVal[i]);
            }
            return ba;
        } else if (prop.getType() == Property.Type.DOUBLE) {
            Double[] da = new Double[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                da[i] = Double.valueOf(aVal[i]);
            }
            return da;
        } else if (prop.getType() == Property.Type.INTEGER) {
            Integer[] ia = new Integer[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                ia[i] = Integer.valueOf(aVal[i]);
            }
            return ia;
        } else {
            return aVal;
        }
    }

    @Override
    public @Nullable Pattern getValidationPattern() {
        return prop.getValidationPattern();
    }

    @Override
    public @Nullable Object get() {
        return prop.getString();
    }

    @Override
    public @Nullable Object[] getList() {
        String[] aVal = prop.getStringList();
        if (prop.getType() == Property.Type.BOOLEAN) {
            Boolean[] ba = new Boolean[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                ba[i] = Boolean.valueOf(aVal[i]);
            }
            return ba;
        } else if (prop.getType() == Property.Type.DOUBLE) {
            Double[] da = new Double[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                da[i] = Double.valueOf(aVal[i]);
            }
            return da;
        } else if (prop.getType() == Property.Type.INTEGER) {
            Integer[] ia = new Integer[aVal.length];
            for (int i = 0; i < aVal.length; i++) {
                ia[i] = Integer.valueOf(aVal[i]);
            }
            return ia;
        } else {
            return aVal;
        }
    }

    @Override
    public void set(@Nullable Object value) {
        if (value != null) {
            if (prop.getType() == Property.Type.BOOLEAN) {
                prop.set(Boolean.parseBoolean(value.toString()));
            } else if (prop.getType() == Property.Type.DOUBLE) {
                prop.set(Double.parseDouble(value.toString()));
            } else if (prop.getType() == Property.Type.INTEGER) {
                prop.set(Integer.parseInt(value.toString()));
            } else {
                prop.set(value.toString());
            }
        }
    }

    @Override
    public void set(@Nullable Object[] aVal) {
        if (aVal != null) {
            if (prop.getType() == Property.Type.BOOLEAN) {
                boolean[] ba = new boolean[aVal.length];
                for (int i = 0; i < aVal.length; i++) {
                    ba[i] = Boolean.parseBoolean(aVal[i].toString());
                }
                prop.set(ba);
            } else if (prop.getType() == Property.Type.DOUBLE) {
                double[] da = new double[aVal.length];
                for (int i = 0; i < aVal.length; i++) {
                    da[i] = Double.parseDouble(aVal[i].toString());
                }
                prop.set(da);
            } else if (prop.getType() == Property.Type.INTEGER) {
                int[] ia = new int[aVal.length];
                for (int i = 0; i < aVal.length; i++) {
                    ia[i] = Integer.parseInt(aVal[i].toString());
                }
                prop.set(ia);
            } else {
                String[] is = new String[aVal.length];
                for (int i = 0; i < aVal.length; i++) {
                    is[i] = aVal[i].toString();
                }
                prop.set(is);
            }
        }
    }

    @Override
    public @Nullable Object getMinValue() {
        return prop.getMinValue();
    }

    @Override
    public @Nullable Object getMaxValue() {
        return prop.getMaxValue();
    }
}
