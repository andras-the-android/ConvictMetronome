package hu.kts.cmetronome.repository

import android.content.SharedPreferences
import hu.kts.cmetronome.ui.Toaster
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class WorkoutSettingsTest {

    @Rule
    @JvmField
    val initRule = MockitoJUnit.rule()

    @Mock
    lateinit var sharedPreferencesMock: SharedPreferences

    @Mock
    lateinit var toasterMock: Toaster

    @Captor
    lateinit var preferenceChangeListenerArgumentCaptor: ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener>

    lateinit var subject: WorkoutSettings

    fun initSubject() {
        subject = WorkoutSettings(sharedPreferencesMock, toasterMock)
        verify(sharedPreferencesMock).registerOnSharedPreferenceChangeListener(preferenceChangeListenerArgumentCaptor.capture())
    }


    @Test
    fun `play sound`() {
        `when`(sharedPreferencesMock.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        initSubject()

        //default value
        assertThat(subject.playSound, equalTo(true))

        //value change
        `when`(sharedPreferencesMock.getBoolean(anyString(), anyBoolean())).thenReturn(false)
        callPrefChangeListener(WorkoutSettings.KEY_PLAY_SOUND)
        assertThat("value changed", subject.playSound, equalTo(false))
    }

    @Test
    fun `rep start with up`() {
        `when`(sharedPreferencesMock.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        initSubject()

        //default value
        assertThat(subject.repStartsWithUp, equalTo(true))

        //value change
        `when`(sharedPreferencesMock.getBoolean(anyString(), anyBoolean())).thenReturn(false)
        callPrefChangeListener(WorkoutSettings.KEY_REP_STARTS_WITH_UP)
        assertThat(subject.repStartsWithUp, equalTo(false))
    }

    @Test
    fun `countdown from`() {
        `when`(sharedPreferencesMock.getInt(anyString(), anyInt())).thenReturn(COUNTDOWN_FROM)

        initSubject()

        assertThat(subject.countdownStartValue, equalTo(COUNTDOWN_FROM))
    }

    @Test
    fun `rep up`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_1.toString())

        initSubject()

        //default value
        assertThat(subject.repUpTime, equalTo(UP_1))

        //value change
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_UP_TIME)
        assertThat(subject.repUpTime, equalTo(UP_2))
    }

    @Test
    fun `rep down`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(DOWN_1.toString())

        initSubject()

        //default value
        assertThat(subject.repDownTime, equalTo(DOWN_1))

        //value change
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(DOWN_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_DOWN_TIME)
        assertThat(subject.repDownTime, equalTo(DOWN_2))
    }

    @Test
    fun `rep down same as up`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_1.toString())
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(WorkoutSettings.KEY_SAME_AS)

        initSubject()

        //default value
        assertThat(subject.repUpTime, equalTo(UP_1))
        assertThat(subject.repDownTime, equalTo(UP_1))
        
        //change up
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_UP_TIME)
        assertThat(subject.repUpTime, equalTo(UP_2))
        assertThat(subject.repDownTime, equalTo(UP_2))

        //change down
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(DOWN_1.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_DOWN_TIME)
        assertThat(subject.repUpTime, equalTo(UP_2))
        assertThat(subject.repDownTime, equalTo(DOWN_1))

        //change back to initial values
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_1.toString())
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(WorkoutSettings.KEY_SAME_AS)
        callPrefChangeListener(WorkoutSettings.KEY_REP_UP_TIME)
        callPrefChangeListener(WorkoutSettings.KEY_REP_DOWN_TIME)
        assertThat(subject.repUpTime, equalTo(UP_1))
        assertThat(subject.repDownTime, equalTo(UP_1))
    }

    @Test
    fun `rep length check`() {
        //both value is higher than zero
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn(UP_1.toString())
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn(WorkoutSettings.KEY_SAME_AS)

        initSubject()

        verify(toasterMock, never()).showShort(anyInt())

        //down is zero
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_DOWN_TIME), anyString())).thenReturn("0")
        callPrefChangeListener(WorkoutSettings.KEY_REP_DOWN_TIME)
        verify(toasterMock, never()).showShort(anyInt())

        //both value is zero
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_UP_TIME), anyString())).thenReturn("0")
        callPrefChangeListener(WorkoutSettings.KEY_REP_UP_TIME)
        verify(toasterMock, times(1)).showShort(anyInt())
    }

    @Test
    fun `pause up`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_UP_TIME), anyString())).thenReturn(UP_1.toString())

        initSubject()

        //default value
        assertThat(subject.repPauseUpTime, equalTo(UP_1))

        //value change
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_UP_TIME), anyString())).thenReturn(UP_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_UP_TIME)
        assertThat(subject.repPauseUpTime, equalTo(UP_2))
    }

    @Test
    fun `pause down`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME), anyString())).thenReturn(DOWN_1.toString())

        initSubject()

        //default value
        assertThat(subject.repPauseDownTime, equalTo(DOWN_1))

        //value change
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME), anyString())).thenReturn(DOWN_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME)
        assertThat(subject.repPauseDownTime, equalTo(DOWN_2))
    }

    @Test
    fun `pause down same as up`() {
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_UP_TIME), anyString())).thenReturn(UP_1.toString())
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME), anyString())).thenReturn(WorkoutSettings.KEY_SAME_AS)

        initSubject()

        //default value
        assertThat(subject.repPauseUpTime, equalTo(UP_1))
        assertThat(subject.repPauseDownTime, equalTo(UP_1))

        //change up
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_UP_TIME), anyString())).thenReturn(UP_2.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_UP_TIME)
        assertThat(subject.repPauseUpTime, equalTo(UP_2))
        assertThat(subject.repPauseDownTime, equalTo(UP_2))

        //change down
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME), anyString())).thenReturn(DOWN_1.toString())
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME)
        assertThat(subject.repPauseUpTime, equalTo(UP_2))
        assertThat(subject.repPauseDownTime, equalTo(DOWN_1))

        //change back to initial values
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_UP_TIME), anyString())).thenReturn(UP_1.toString())
        `when`(sharedPreferencesMock.getString(eq(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME), anyString())).thenReturn(WorkoutSettings.KEY_SAME_AS)
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_UP_TIME)
        callPrefChangeListener(WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME)
        assertThat(subject.repPauseUpTime, equalTo(UP_1))
        assertThat(subject.repPauseDownTime, equalTo(UP_1))
    }

    @Test
    fun `external listeners`() {
        val listener1 = mock(SharedPreferences.OnSharedPreferenceChangeListener::class.java)
        val listener2 = mock(SharedPreferences.OnSharedPreferenceChangeListener::class.java)

        initSubject()
        subject.addListener(listener1)
        subject.addListener(listener2)

        callPrefChangeListener(RANDOM_KEY)

        verify(listener1, times(1)).onSharedPreferenceChanged(sharedPreferencesMock, RANDOM_KEY)
        verify(listener2, times(1)).onSharedPreferenceChanged(sharedPreferencesMock, RANDOM_KEY)
    }
    

    private fun callPrefChangeListener(prefKey: String) {
        preferenceChangeListenerArgumentCaptor.value.onSharedPreferenceChanged(sharedPreferencesMock, prefKey)
    }

    companion object {
        private const val UP_1 = 10000L
        private const val UP_2 = 20000L
        private const val DOWN_1 = 30000L
        private const val DOWN_2 = 40000L
        private const val RANDOM_KEY = "foobar"
        private const val COUNTDOWN_FROM = 234
    }


}