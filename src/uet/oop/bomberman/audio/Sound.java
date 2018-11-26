package uet.oop.bomberman.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Sound {

    public static void makeSound(String sound)
    {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/audios/" + sound + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

}
