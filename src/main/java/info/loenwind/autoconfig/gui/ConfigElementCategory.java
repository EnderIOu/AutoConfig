package info.loenwind.autoconfig.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 ** Forge's {@link ConfigElement} is written in a way to prevent sub-classing, so we had to copy it.
 **/
public class ConfigElementCategory implements IConfigElement {

    private final ConfigCategory category;

    public ConfigElementCategory(ConfigCategory category) {
        this.category = category;
    }

    @Override
    public List<IConfigElement> getChildElements() {
        List<IConfigElement> elements = new ArrayList<>();

        for (ConfigCategory subcat : category.getChildren()) {
            if (subcat.showInGui())
                elements.add(new ConfigElementCategory(subcat));
        }
        for (Property element : category.getOrderedValues()) {
            if (element.showInGui())
                elements.add(new ConfigElementProperty(element));
        }

        return elements;
    }

    @Override
    public @Nullable String getName() {
        return category.getName();
    }

    @Override
    public boolean isProperty() {
        return false;
    }

    @Override
    public @Nullable Class<? extends IConfigEntry> getConfigEntryClass() {
        return category.getConfigEntryClass();
    }

    @Override
    public @Nullable Class<? extends IArrayEntry> getArrayEntryClass() {
        return null;
    }

    @Override
    public @Nullable String getQualifiedName() {
        return category.getQualifiedName();
    }

    @Override
    public ConfigGuiType getType() {
        return ConfigGuiType.CONFIG_CATEGORY;
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
        return false;
    }

    @Override
    public boolean isListLengthFixed() {
        return false;
    }

    @Override
    public int getMaxListLength() {
        return -1;
    }

    @Override
    public @Nullable String getComment() {
        return category.getComment();
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void setToDefault() {}

    @Override
    public boolean requiresWorldRestart() {
        return category.requiresWorldRestart();
    }

    @Override
    public boolean showInGui() {
        return category.showInGui();
    }

    @Override
    public boolean requiresMcRestart() {
        return category.requiresMcRestart();
    }

    @Override
    public @Nullable String[] getValidValues() {
        return null;
    }

    @Override
    public @Nullable String getLanguageKey() {
        return category.getLanguagekey();
    }

    @Override
    public @Nullable Object getDefault() {
        return null;
    }

    @Override
    public @Nullable Object[] getDefaults() {
        return null;
    }

    @Override
    public @Nullable Pattern getValidationPattern() {
        return null;
    }

    @Override
    public @Nullable Object get() {
        return null;
    }

    @Override
    public @Nullable Object[] getList() {
        return null;
    }

    @Override
    public void set(@Nullable Object value) {}

    @Override
    public void set(@Nullable Object[] aVal) {}

    @Override
    public @Nullable Object getMinValue() {
        return null;
    }

    @Override
    public @Nullable Object getMaxValue() {
        return null;
    }
}
