package info.loenwind.autoconfig.factory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;

import info.loenwind.autoconfig.util.NullHelper;

public class ValueFactory implements IRootFactory {

    private final String modid;
    private @Nullable Configuration config = null;
    private boolean inInit = false;
    private int generation = 0;
    final private List<AbstractValue<?>> preloadValues = new ArrayList<>();

    public ValueFactory(String modid) {
        this.modid = modid;
    }

    @Override
    public @Nonnull IValueFactory section(@SuppressWarnings("hiding") @Nonnull String section) {
        return new SlaveFactory(this, section);
    }

    @Override
    public @Nonnull String getModid() {
        return modid;
    }

    @Override
    public @Nonnull String getSection() {
        return "";
    }

    @Override
    public void setConfig(Configuration config) {
        this.config = config;
        generation++;
        inInit = true;
        for (AbstractValue<?> value : preloadValues) {
            value.get();
        }
        inInit = false;
        if (config.hasChanged()) {
            config.save();
        }
        // Note: Forge trashes the config when loading it from disk, so we need to re-configure all values every time
        // that happens
        // preloadValues.clear();
    }

    @Override
    public Configuration getConfig() {
        return NullHelper.notnull(config, "Cannot access config before it was initialized!");
    }

    @Override
    public boolean isInInit() {
        return inInit;
    }

    @Override
    public int getGeneration() {
        return generation;
    }

    @Override
    public void addValue(@Nonnull AbstractValue<?> value) {
        preloadValues.add(value);
    }
}
