package hu.kts.cmetronome

import android.os.Handler
import android.os.SystemClock
import hu.kts.cmetronome.architetcture.SingleLiveEvent
import java.util.concurrent.TimeUnit

class TimeProvider : SingleLiveEvent<Long>() {

    private var state = State.STOPPED
    private val handler = Handler()
    private var countDownStartValue = -1
    private var count: Long = 0
    var startTime: Long = 0
        private set

    private val delayMillis: Long
        get() {
            val timestampOfDesiredNextTick = startTime + TimeUnit.SECONDS.toMillis(count + 1)
            return timestampOfDesiredNextTick - SystemClock.elapsedRealtime()
        }

    private val isCountDown: Boolean
        get() = countDownStartValue > -1

    private val isCountDownLastRound: Boolean
        get() = count == countDownStartValue.toLong()

    @Synchronized
    fun startUp() {
        initCount()
    }

    @Synchronized
    fun startDown(startValue: Int) {
        checkCountdownStartValue(startValue)
        countDownStartValue = startValue
        initCount()
    }

    @Synchronized
    fun stop() {
        state = State.STOPPED
    }

    @Synchronized
    fun continueSeamlesslyUp(originalStartTime: Long) {
        if (state == State.STOPPED) {
            startTime = originalStartTime
            state = State.IN_PROGRESS
            startCycle()
        }
    }

    private fun initCount() {
        startTime = SystemClock.elapsedRealtime()
        state = State.IN_PROGRESS
        startCycle()
    }

    private fun startCycle() {
        if (state == State.IN_PROGRESS) {
            val t = calcCallbackValue()
            value = t
            if (isCountDownLastRound) {
                state = State.STOPPED
            } else {
                handler.postDelayed({ this.startCycle() }, delayMillis)
            }
        }
    }

    private fun checkCountdownStartValue(startValue: Int) {
        if (startValue < 0) {
            throw IllegalArgumentException("startValue must be greater or equal to 0")
        }
    }

    private fun calcCallbackValue(): Long {
        val elapsedTimeMillis = SystemClock.elapsedRealtime() - startTime
        count = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis)
        //TimeUnit always rounds down and sometimes this causes that the same number comes twice. This way we compensate it.
        if (elapsedTimeMillis % 1000 >= 500) ++count
        return if (isCountDown) countDownStartValue - count else count
    }

    override fun onActive() {
        //invoked twice somehow
        if (state == State.INACTIVE) {
            state = State.IN_PROGRESS
            startCycle()
        }
    }

    override fun onInactive() {
        if (state == State.IN_PROGRESS) {
            state = State.INACTIVE
        }
    }

    /**
     * Stopped when someone called stop manually or never started.
     * Inactive when the caller activity is in background but progress
     * will continue automatically when it become to foreground again.
     */
    private enum class State {
        STOPPED, INACTIVE, IN_PROGRESS
    }
}
