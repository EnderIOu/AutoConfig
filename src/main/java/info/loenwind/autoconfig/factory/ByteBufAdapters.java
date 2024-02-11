package info.loenwind.autoconfig.factory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;

public class ByteBufAdapters {

    public static <P> IByteBufAdapter<P> register(IByteBufAdapter<P> adapter) {
        ByteBufAdapterRegistry.register(adapter);
        return adapter;
    }

    public static final IByteBufAdapter<Integer> INTEGER = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Integer value) {
            buf.writeInt(value);
        }

        @Override
        public Integer readValue(ByteBuf buf) {
            return buf.readInt();
        }

        @Override
        public String getName() {
            return "I";
        }
    });

    public static final IByteBufAdapter<int[]> INTEGERARRAY = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull int[] value) {
            buf.writeInt(value.length);
            for (int i : value) {
                buf.writeInt(i);
            }
        }

        @Override
        public int[] readValue(ByteBuf buf) {
            int len = buf.readInt();
            int[] result = new int[len];
            for (int i = 0; i < result.length; i++) {
                result[i] = buf.readInt();
            }
            return result;
        }

        @Override
        public String getName() {
            return "i[]";
        }
    });

    public static final IByteBufAdapter<Double> DOUBLE = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Double value) {
            buf.writeDouble(value);
        }

        @Override
        public Double readValue(ByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public String getName() {
            return "D";
        }
    });

    public static final IByteBufAdapter<Float> FLOAT = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Float value) {
            buf.writeFloat(value);
        }

        @Override
        public Float readValue(ByteBuf buf) {
            return buf.readFloat();
        }

        @Override
        public String getName() {
            return "F";
        }
    });

    public static final IByteBufAdapter<Boolean> BOOLEAN = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Boolean value) {
            buf.writeBoolean(value);
        }

        @Override
        public Boolean readValue(ByteBuf buf) {
            return buf.readBoolean();
        }

        @Override
        public String getName() {
            return "B";
        }
    });

    public static final IByteBufAdapter<String> STRING = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull String value) {
            final byte[] vbytes = value.getBytes(StandardCharsets.UTF_8);
            if (vbytes.length > 0x7FFF) {
                throw new RuntimeException("String too long");
            }
            buf.writeShort(vbytes.length);
            buf.writeBytes(vbytes);
        }

        @Override
        public String readValue(ByteBuf buf) {
            final int len = buf.readShort();
            final byte[] bytes = new byte[len];
            buf.readBytes(bytes, 0, len);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public String getName() {
            return "S";
        }
    });

    public static final IByteBufAdapter<List<String>> STRINGLIST = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull List<String> value) {
            buf.writeInt(value.size());
            for (String string : value) {
                STRING.saveValue(buf, NullHelper.first(string, ""));
            }
        }

        @Override
        public List<String> readValue(ByteBuf buf) {
            final int len = buf.readInt();
            final List<String> result = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                result.add(STRING.readValue(buf));
            }
            return result;
        }

        @Override
        public String getName() {
            return "S()";
        }
    });

    public static final IByteBufAdapter<String> STRING127 = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull String value) {
            final byte[] vbytes = value.getBytes(StandardCharsets.UTF_8);
            if (vbytes.length > 0x7F) {
                throw new RuntimeException("String too long");
            }
            buf.writeByte(vbytes.length); // Important: Keep in sync with ENDMARKER
            buf.writeBytes(vbytes);
        }

        @Override
        public String readValue(ByteBuf buf) {
            final int len = buf.readByte();
            final byte[] bytes = new byte[len];
            buf.readBytes(bytes, 0, len);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public String getName() {
            return "7";
        }
    });

    public static final IByteBufAdapter<Boolean> ENDMARKER = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Boolean value) {
            buf.writeByte(0);
        }

        @Override
        public @Nullable Boolean readValue(ByteBuf buf) {
            buf.markReaderIndex();
            final int val = buf.readByte();
            if (val == 0) {
                return true;
            } else {
                buf.resetReaderIndex();
                return null;
            }
        }

        @Override
        public String getName() {
            return "X";
        }
    });

    public static final IByteBufAdapter<Boolean> NONE = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Boolean value) {}

        @Override
        public @Nullable Boolean readValue(ByteBuf buf) {
            return null;
        }

        @Override
        public String getName() {
            return "N";
        }
    });

    public static final IByteBufAdapter<Fluid> FLUID = register(new IByteBufAdapter<>() {

        @Override
        public void saveValue(ByteBuf buf, @Nonnull Fluid value) {
            final byte[] vbytes = NullHelper.first(FluidRegistry.getFluidName(value), "")
                    .getBytes(StandardCharsets.UTF_8);
            if (vbytes.length > 0x7F) {
                throw new RuntimeException("Fluid name too long");
            }
            buf.writeByte(vbytes.length);
            buf.writeBytes(vbytes);
        }

        @Override
        public @Nullable Fluid readValue(ByteBuf buf) {
            final int len = buf.readByte();
            final byte[] bytes = new byte[len];
            buf.readBytes(bytes, 0, len);
            return FluidRegistry.getFluid(new String(bytes, StandardCharsets.UTF_8));
        }

        @Override
        public String getName() {
            return "L";
        }
    });
}
