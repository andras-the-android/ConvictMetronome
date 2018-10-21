package hu.kts.cmetronome

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator

class Sounds(context: Context) {

    private val soundPool: SoundPool
    private val upSoundID: Int
    private val downSoundID: Int
    private var currentSoundId: Int = 0
    private val toneGenerator: ToneGenerator


    init {
        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        upSoundID = soundPool.load(context, R.raw.up_sine, 1)
        downSoundID = soundPool.load(context, R.raw.down_sine, 1)
        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
    }

    fun makeUpSound() {
        currentSoundId = soundPool.play(upSoundID, 1f, 1f, 5, 0, 1f)
    }

    fun makeDownSound() {
        currentSoundId = soundPool.play(downSoundID, 1f, 1f, 5, 0, 1f)
    }

    fun stop() {
        soundPool.stop(currentSoundId)
    }

    fun beep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
    }

}
