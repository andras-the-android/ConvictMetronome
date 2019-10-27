package hu.kts.cmetronome

import android.content.SharedPreferences
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Short

@Suppress("JoinDeclarationAndAssignment")
class Sounds(private val settings: Settings) {

    private val toneGenerator: ToneGenerator
    private var audioTrack: AudioTrack? = null
    private var sampleArrayUp = ShortArray(0)
    private var sampleArrayDown = ShortArray(0)
    //we have to hold a reference to this or else it'd be gc-d
    private val settingsListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == Settings.KEY_REP_UP_TIME || key == Settings.KEY_REP_DOWN_TIME) generateUpDownSounds()
    }

    init {
        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        settings.addListener(settingsListener)
        generateUpDownSounds()
    }

    fun makeUpSound() {
        playSound(true)
    }

    fun makeDownSound() {
        playSound(false)
    }

    fun stop() {
        tryStop()
    }

    fun beep() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150)
    }

    private fun generateUpDownSounds() {
        GlobalScope.launch{
            sampleArrayUp = generateSoundArray(settings.repUpTime.toInt(), true)
            sampleArrayDown = generateSoundArray(settings.repDownTime.toInt(),false)
        }
    }

    private fun generateSoundArray(durationMillis: Int, up: Boolean): ShortArray {
        val sampleCount = durationMillis * SAMPLE_RATE / 1000
        val result = ShortArray(sampleCount)
        val frequencyIncrement = BASE_FREQUENCY / result.size
        var frequency = BASE_FREQUENCY

        for (i in result.indices) {
            frequency += frequencyIncrement
            var sample = Math.sin(2.0 * Math.PI * i.toDouble() / (SAMPLE_RATE / frequency)) * DISTORTION_AMOUNT
            //inverting the peak of the sine wave
            if (sample > 1) {
                sample = 1 - (sample - 1)
            }
            if (sample < -1) {
                sample = -1 - (sample + 1)
            }
            val index = if (up) i else sampleCount - i - 1
            result[index] = (sample * Short.MAX_VALUE).toShort()
        }
        return result
    }

    private fun playSound(up: Boolean) {
        if (!settings.playSound) return

        tryStop()
        GlobalScope.launch {
            // AudioTrack definition
            val bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT)

            audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize, AudioTrack.MODE_STREAM)


            audioTrack!!.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume())
            audioTrack!!.play()
            audioTrack!!.write(if (up) sampleArrayUp else sampleArrayDown, 0, if (up) sampleArrayUp.size else sampleArrayDown.size)

            tryStop()
        }
    }

    private fun tryStop() {
        audioTrack?.run {
            if (playState != AudioTrack.PLAYSTATE_STOPPED) {
                stop()
                release()
            }
        }
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BASE_FREQUENCY = 261.63
        private const val DISTORTION_AMOUNT = 1.7 //1 is pure sine
    }

}
