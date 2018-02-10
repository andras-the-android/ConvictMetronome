package hu.kts.cmetronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;

public class Sounds {

    private SoundPool soundPool;
    private int upSoundID;
    private int downSoundID;
    private int currentSoundId;
    private final ToneGenerator toneGenerator;


    public Sounds(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        upSoundID = soundPool.load(context, R.raw.up_sine, 1);
        downSoundID = soundPool.load(context, R.raw.down_sine, 1);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    }

    public void makeUpSound() {
        if (soundPool != null) {
            currentSoundId = soundPool.play(upSoundID, 1f, 1f, 5, 0, 1f);
        }
    }

    public void makeDownSound() {
        if (soundPool != null) {
            currentSoundId = soundPool.play(downSoundID, 1f, 1f, 5, 0, 1f);
        }
    }

    public void stop() {
        soundPool.stop(currentSoundId);
    }

    public void beep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
    }

}
