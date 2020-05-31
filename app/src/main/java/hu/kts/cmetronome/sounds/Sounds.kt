package hu.kts.cmetronome.sounds

import android.content.SharedPreferences
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import hu.kts.cmetronome.di.WorkoutScope
import hu.kts.cmetronome.repository.WorkoutSettings
import hu.kts.cmetronome.sounds.SoundWaveGenerator.Companion.SAMPLE_RATE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("JoinDeclarationAndAssignment")
@WorkoutScope
class Sounds @Inject constructor(private val settings: WorkoutSettings, private val generator: SoundWaveGenerator) {

    private val toneGenerator: ToneGenerator
    private var audioTrack: AudioTrack? = null
    private var sampleArrayUp = ShortArray(0)
    private var sampleArrayDown = ShortArray(0)
    //we have to hold a reference to this or else it'd be gc-d
    private val settingsListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == WorkoutSettings.KEY_REP_UP_TIME || key == WorkoutSettings.KEY_REP_DOWN_TIME) generateUpDownSounds()
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
            sampleArrayUp = generator.generate(settings.repUpTime.toInt(), true)
            sampleArrayDown = generator.generate(settings.repDownTime.toInt(),false)
        }
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

}
