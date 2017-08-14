package MultiPlayer.AudioChat;

import MultiPlayer.ConnectServer;
import MultiPlayer.ServerCommand;
import javafx.scene.media.AudioClip;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sound.sampled.*;

public class RecordVoice {
    public static boolean pressed_R = false;

    public static void main(String[] args) {
  record();
    }

    public static void record(){
        File outputFile = new File("Sounds/rec.wav");
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 2, 4, 44100.0F, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        TargetDataLine targetDataLine = null;
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException ignored) {
        }
        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

        while (pressed_R) {
            targetDataLine.start();
            try {
                TargetDataLine finalTargetDataLine = targetDataLine;
                new Thread(() -> {
                    try {
                        AudioSystem.write(new AudioInputStream(finalTargetDataLine), targetType, outputFile);
                    } catch (IOException ignored) {
                    }
                }).start();
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
            targetDataLine.stop();
            try {
                byte[] fileInArray = new byte[(int)outputFile.length()];
                FileInputStream f = new FileInputStream(outputFile);
                f.read(fileInArray);
                ConnectServer.out.writeObject(new ServerCommand(fileInArray,ConnectServer.CHAT_AUDIO));
                ConnectServer.out.flush();
            } catch (Exception ignored){
                ignored.printStackTrace();
            }
        }
    }
}