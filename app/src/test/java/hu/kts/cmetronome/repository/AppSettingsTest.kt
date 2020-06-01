package hu.kts.cmetronome.repository

import android.content.SharedPreferences
import hu.kts.cmetronome.BuildConfig
import hu.kts.cmetronome.createMockEditor
import hu.kts.cmetronome.repository.WorkoutSettings.Companion.KEY_REP_PAUSE_TIME
import hu.kts.cmetronome.repository.WorkoutSettings.Companion.KEY_REP_PAUSE_UP_TIME
import hu.kts.cmetronome.repository.WorkoutSettings.Companion.KEY_REP_UP_DOWN_TIME
import hu.kts.cmetronome.repository.WorkoutSettings.Companion.KEY_REP_UP_TIME
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class AppSettingsTest {

    @Rule
    @JvmField
    val initRule = MockitoJUnit.rule()

    @Mock
    lateinit var sharedPreferencesMock: SharedPreferences

    lateinit var subject: AppSettings

    fun initSubject() {
        subject = AppSettings(sharedPreferencesMock)
    }

    @Test
    fun `analytics `() {
        initSubject()

        `when`(sharedPreferencesMock.getBoolean(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(false)
        assertThat(subject.isAnalyticsEnabled, equalTo(false))
        `when`(sharedPreferencesMock.getBoolean(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(true)
        assertThat(subject.isAnalyticsEnabled, equalTo(true))
    }

    @Test
    fun `what's new`() {
        initSubject()

        `when`(sharedPreferencesMock.getInt(anyString(), anyInt())).thenReturn(RANDOM_INT_1)
        assertThat(subject.whatsNewVersion, equalTo(RANDOM_INT_1))
    }

    @Test
    fun `show help`() {
        initSubject()

        `when`(sharedPreferencesMock.getBoolean(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(false)
        assertThat(subject.isShowHelp, equalTo(false))
        `when`(sharedPreferencesMock.getBoolean(anyString(), ArgumentMatchers.anyBoolean())).thenReturn(true)
        assertThat(subject.isShowHelp, equalTo(true))
    }

    @Test
    fun `update what's new version`() {
        val editorMock = sharedPreferencesMock.createMockEditor()
        initSubject()

        subject.updateWhatsNewVersion()

        verify(editorMock).putInt(AppSettings.KEY_WHATS_NEW_VERSION, BuildConfig.VERSION_CODE)
        verify(editorMock).apply()
    }

    @Test
    fun `add listener`() {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->  }
        initSubject()

        subject.addListener(listener)

        verify(sharedPreferencesMock).registerOnSharedPreferenceChangeListener(listener)
    }

    @Test
    fun `migration 11`() {
        val editorMock = sharedPreferencesMock.createMockEditor()
        `when`(sharedPreferencesMock.getString(eq(KEY_REP_UP_DOWN_TIME), anyString())).thenReturn(RANDOM_STRING_1)
        `when`(sharedPreferencesMock.getString(eq(KEY_REP_PAUSE_TIME), anyString())).thenReturn(RANDOM_STRING_2)
        initSubject()

        subject.runMigration11()

        verify(editorMock).putString(KEY_REP_UP_TIME, RANDOM_STRING_1)
        verify(editorMock).putString(KEY_REP_PAUSE_UP_TIME, RANDOM_STRING_2)
        verify(editorMock).apply()
    }

    companion object {
        private const val RANDOM_INT_1 = 4567
        private const val RANDOM_STRING_1 = "5678"
        private const val RANDOM_STRING_2 = "6789"
    }
}