package info.loenwind.autoconfig.factory;

import java.util.Random;

import javax.annotation.Nonnull;

public interface IValue<T> {

    T get();

    /**
     * Marks this config value as one that needs to be synced from the server to the client. Returns the object itself
     * for chaining.
     * <p>
     * Note: Not all config values support this.
     */
    default IValue<T> sync() {
        return this;
    }

    /**
     * Marks this config value as one that needs to be in sync between the server and the client but cannot be changed
     * at runtime. Returns the object itself for
     * chaining.
     * <p>
     * Note: Not all config values support this.
     */
    default IValue<T> startup() {
        return this;
    }

    default IValue<T> setRange(double min, double max) {
        setMin(min);
        setMax(max);
        return this;
    }

    default IValue<T> setMin(double min) {
        return this;
    }

    default IValue<T> setMax(double max) {
        return this;
    }

    @Nonnull
    Random RAND = new Random();

    /**
     * See {@link #getChance(Random)}, uses an internal RNG.
     */
    default int getChance() {
        return getChance(RAND);
    }

    /**
     * Interprets the config value as a chance and determines the outcome based on the given RNG. This does support
     * chances above 100% and can return numbers
     * greater than one for those.
     * <p>
     * Note: Not all config values support this.
     */
    default int getChance(Random rand) {
        throw new UnsupportedOperationException();
    }
}
