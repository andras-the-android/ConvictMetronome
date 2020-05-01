package hu.kts.cmetronome.sounds

import javax.inject.Inject
import kotlin.math.sin

class SoundWaveGenerator @Inject constructor() {

    fun generate(durationMillis: Int, up: Boolean): ShortArray {
        val sampleCount = durationMillis * SAMPLE_RATE / 1000
        val result = ShortArray(sampleCount)
        val frequencyIncrement = BASE_FREQUENCY / result.size
        var frequency = BASE_FREQUENCY

        for (i in result.indices) {
            frequency += frequencyIncrement
            var sample = sin(2.0 * Math.PI * i.toDouble() / (SAMPLE_RATE / frequency)) * DISTORTION_AMOUNT
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

    companion object {
        const val SAMPLE_RATE = 44100
        private const val BASE_FREQUENCY = 261.63
        private const val DISTORTION_AMOUNT = 1.7 //1 is pure sine
    }

}