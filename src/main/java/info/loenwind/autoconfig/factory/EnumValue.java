package info.loenwind.autoconfig.factory;

import java.util.Arrays;

import javax.annotation.Nonnull;

import info.loenwind.autoconfig.util.NullHelper;

class EnumValue<E extends Enum<E>> implements IValue<E> {

    private final Class<E> enumClazz;
    private final E defaultEnumValue;
    private final IValue<String> storage;

    protected EnumValue(IValueFactory owner, String section, String keyname, E defaultValue, String text) {
        this.defaultEnumValue = defaultValue;
        this.enumClazz = NullHelper.notnull(defaultValue.getDeclaringClass(), "enum without a class");
        storage = owner.make(keyname, NullHelper.notnullJ(defaultValue.name(), "enum without a name"),
                NullHelper.notnullJ(Arrays.stream(enumClazz.getEnumConstants()).map(Enum::name).toArray(String[]::new),
                        "Stream.toArray"),
                text);
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull E get() {
        try {
            return Enum.valueOf(enumClazz, storage.get());
        } catch (IllegalArgumentException e) {
            return defaultEnumValue;
        }
    }

    @Override
    @Nonnull
    public IValue<E> sync() {
        storage.sync();
        return this;
    }

    @Override
    @Nonnull
    public IValue<E> startup() {
        storage.startup();
        return this;
    }
}
