package engine.utils.libraryWrappers.opengl.utils;

import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    private static AtomicInteger UUID_3D = new AtomicInteger();
    private static AtomicInteger UUID_GUI = new AtomicInteger();

    /**
     * Utility for loading string resources
     *
     * @param filePath image path with format "res/*"
     * @return String resource
     * @throws Exception if path is not found
     */
    public static String loadResource(String filePath) throws Exception {
        String result;
        if (filePath.startsWith("res")) {
            filePath = filePath.replaceFirst("res", "");
        }
        InputStream in = Utils.class.getResourceAsStream(filePath);
        if (in != null) {
            try (Scanner scanner = new Scanner(in)) {
                result = scanner.useDelimiter("\\A").next();
            }
            System.out.println("Resouce loaded: " + filePath);
        } else {
            throw new IllegalStateException(filePath);
        }
        return result;
    }

    /**
     * `
     *
     * @param fileName path with format "res/*"
     * @return List of strings that compose the file
     * @throws Exception file not found
     */
    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(fileName);
        InputStreamReader in = new InputStreamReader(is);
        try (BufferedReader br = new BufferedReader(in)) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static ByteBuffer ioResourceToBuffer(String filename, int bufferSize) throws IOException {

        ByteBuffer buffer;
        Path path = Paths.get(filename);

        if (Files.isReadable(path)) {
            try (SeekableByteChannel channel = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) channel.size() + 1);
            }
        } else {
            try (
                    InputStream is = Utils.class.getClassLoader().getResourceAsStream(filename);
                    ReadableByteChannel rbc = Channels.newChannel(is)
            ) {
                buffer = BufferUtils.createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);

                    if (bytes == -1)
                        break;
                    if (buffer.remaining() == 0)
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);

                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static String absolutePath(String relativePath) {
        relativePath = relativePath.replaceFirst("res", "");
        return Utils.class.getResource(relativePath).getPath();
    }

    public static int[] bufferToIntArray(IntBuffer b) {
        if (b.hasArray()) {
            if (b.arrayOffset() == 0)
                return b.array();

            return Arrays.copyOfRange(b.array(), b.arrayOffset(), b.array().length);
        }

        b.rewind();
        int[] foo = new int[b.remaining()];
        b.get(foo);

        return foo;
    }

    public static float[] listToArray(List<Float> floats) {
        float[] ret = new float[floats.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = floats.get(i).floatValue();
        }
        return ret;
    }

    public static int[] listIntToArray(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public static int generateNewUUID_3D() {
        return UUID_3D.getAndIncrement();
    }

    public static int generateNewUUID_GUI() {
        return UUID_GUI.getAndIncrement();
    }
}
