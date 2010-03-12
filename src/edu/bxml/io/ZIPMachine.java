package edu.bxml.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZIPMachine {
        public static void zipFile(String fileName) throws IOException {
                FileInputStream in = new FileInputStream(fileName);
                BufferedInputStream in2 = new BufferedInputStream(in);
                FileOutputStream out = new FileOutputStream(fileName + ".zip");
                GZIPOutputStream zipOut = new GZIPOutputStream(out);
                BufferedOutputStream out2 = new BufferedOutputStream(zipOut);
                int chunk;
                while ((chunk = in2.read()) != -1) {
                        out2.write(chunk);
                }
                out2.close();
                zipOut.close();
                out.close();
                System.out.println("Wrote: " + fileName + ".zip");
        }
        public static void unzipFile(String fileName) throws IOException {
                FileInputStream in = new FileInputStream(fileName);
                GZIPInputStream inZip = new GZIPInputStream(in);
                BufferedInputStream in2 = new BufferedInputStream(inZip);
                FileOutputStream out = new FileOutputStream(fileName
                                .replace(".zip", ""));
                BufferedOutputStream out2 = new BufferedOutputStream(out);
                int chunk;
                while ((chunk = in2.read()) != -1) {
                        out2.write(chunk);
                }
                out2.close();
                out.close();
                System.out.println("Wrote: " + fileName.replace(".zip", ""));
        }
        public static void main(String[] args) {
                try {
                        if (args.length == 1) {
                                zipFile(args[0]);
                        } else if (args.length == 2 && args[0].equals("-unzip")) {
                                unzipFile(args[1]);
                        } else {
                                System.out.println("Usage: zip [-unzip] file_name");
                        }
                } catch (IOException e) {
                        System.out.println("An I/O error occured:");
                        System.out.println(e.toString());
                        e.printStackTrace();
                }
        }
}

