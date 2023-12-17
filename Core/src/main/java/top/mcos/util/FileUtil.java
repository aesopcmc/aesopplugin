package top.mcos.util;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class FileUtil {
    public static void printFileContent(Object obj) throws IOException {
        if (null == obj) {
            throw new RuntimeException("参数为空");
        }
        BufferedReader reader = null;
        // 如果是文件路径
        if (obj instanceof String) {
            reader = new BufferedReader(new FileReader(new File((String) obj)));
            // 如果是文件输入流
        } else if (obj instanceof InputStream) {
            reader = new BufferedReader(new InputStreamReader((InputStream) obj));
        }
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
    }


    public static void getResource(String fileName) throws IOException{
        InputStream in = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
        printFileContent(in);
    }

    public static void main(String[] args) throws IOException, FontFormatException {
        //FileUtil.getResource("");
        //
        //String path = FileUtil.class.getResource("/").getPath() + "font/DouyinSansBold.ttf";
        //FileInputStream fi = new FileInputStream(path);
        //Font font = Font.createFont(Font.PLAIN, fi);
        //font = font.deriveFont(Font.PLAIN, 25);
        //System.out.println(font);

        //String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        //System.out.println("支持的字体：" + Arrays.toString(names));
    }

}
