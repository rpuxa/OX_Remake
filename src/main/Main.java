package main;

import Jogl.JavaDia;
import MultiPlayer.ConnectServer;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;


public class Main {

    public static void main(String[] args) {
        JavaDia.displayT.start();
    }


    static {
        try (FileReader reader = new FileReader("Config/config.txt")) {
            int c;
            boolean rec = false;
            String ip = "";
            while ((c = reader.read()) != -1)
            if (c != '\n' && c != '\r' && c != ' '){
                if (c == '}')
                    break;
                if (rec)
                    ip += (char) c;

                if (c == '{')
                    rec = true;
            }
            ConnectServer.ipAddress = InetAddress.getByName(ip);
        } catch (Exception e) {
            System.out.println("Не удалось загрузить config файл");
        }
    }
}
