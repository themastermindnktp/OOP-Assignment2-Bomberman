package uet.oop.bomberman.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class BackgroundMusic {

    private static Clip _clip;
    private static boolean _playing = false;

    public static void playMusic()
    {
        if (_playing) return;
        try {
            _playing = true;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/audios/BackgroundMusic.wav"));
            _clip = AudioSystem.getClip();
            _clip.open(audioInputStream);
            FloatControl volume = (FloatControl) _clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volume.getMaximum() - volume.getMinimum();
            volume.setValue((range * 0.9f) + volume.getMinimum());
            _clip.start();

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public static void stopMusic()
    {
        if (_playing)
        {
            _playing = false;
            _clip.stop();
        }
    }

}
