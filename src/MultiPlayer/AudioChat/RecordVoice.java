package MultiPlayer.AudioChat;

import MultiPlayer.ConnectServer;
import MultiPlayer.ServerCommand;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class RecordVoice {
    public static boolean pressed_R = false;
    public static TargetDataLine targetDataLine = null;

    public static void record(){
        try {
            ConnectServer.out.writeObject(new ServerCommand(null, ConnectServer.CHAT_AUDIO_OPPONENT_RECORDING));
            ConnectServer.out.flush();
        } catch (IOException ignore){
        }
        File outputFile = new File("Sounds/rec.wav");
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 6000.0F, 16, 2, 4, 6000.0F, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
        } catch (LineUnavailableException ignored) {
        }
        AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;

            targetDataLine.start();

            TargetDataLine finalTargetDataLine = targetDataLine;
                new Thread(() -> {
                    try {
                        AudioSystem.write(new AudioInputStream(finalTargetDataLine), targetType, outputFile);
                    } catch (IOException ignored) {
                    }
                }).start();
    }
}