import java.io.*;
import java.nio.file.*;

public class BitstringJava {
    static byte[] GetBinary(String s) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() % 8 != 0) {
            sBuilder.append('0');
        }
        s = sBuilder.toString();

        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            }
        }
        return data;
    }

    static String GetString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    public static void main(String[] args) {
        String binary_string_a = "001011010101011111";
        byte[] converted = GetBinary(binary_string_a);

        // Save bit array to file
        try {
            OutputStream outputStream = new FileOutputStream("output_file.bin");
            outputStream.write(converted);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load bit array from file
        try {
            byte[] allBytes = Files.readAllBytes(Paths.get("output_file.bin"));

            System.out.println("Original String = " + binary_string_a);
            System.out.print("Converted (+spaces) = ");
            for (int i = 0; i < 3; i++)
            {
                System.out.print(converted[i]);
                System.out.print(' ');
            }

            System.out.print("\nLoaded from file (+spaces) = ");
            for (int i = 0; i < 3; i++)
            {
                char c = (char)allBytes[i];
                System.out.print(allBytes[i]);
                System.out.print(' ');
            }

            System.out.println("\nBack to bitString = " + GetString(allBytes));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

