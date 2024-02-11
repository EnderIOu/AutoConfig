package info.loenwind.autoconfig.factory;

import javax.annotation.Nonnull;

public final class IValueBool {

    public static class And implements IValue<Boolean> {

        private final IValue<Boolean> a, b;

        public And(IValue<Boolean> a, IValue<Boolean> b) {
            this.a = a;
            this.b = b;
        }

        @Override
        @Nonnull
        public Boolean get() {
            return a.get() && b.get();
        }
    }

    public static class Or implements IValue<Boolean> {

        private final IValue<Boolean> a, b;

        public Or(IValue<Boolean> a, IValue<Boolean> b) {
            this.a = a;
            this.b = b;
        }

        @Override
        @Nonnull
        public Boolean get() {
            return a.get() || b.get();
        }
    }
}
