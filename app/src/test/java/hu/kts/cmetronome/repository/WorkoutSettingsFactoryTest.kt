package hu.kts.cmetronome.repository
import android.content.SharedPreferences
import hu.kts.cmetronome.ui.Toaster
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsSame
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class WorkoutSettingsFactoryTest {

    @Rule
    @JvmField
    val initRule = MockitoJUnit.rule()

    @Mock
    lateinit var sharedPreferencesFactoryMock: WorkoutSharedPreferencesFactory
    @Mock
    lateinit var toasterMock: Toaster

    lateinit var subject: WorkoutSettingsFactory

    @Before
    fun initSubject() {
        subject = WorkoutSettingsFactory(sharedPreferencesFactoryMock, toasterMock)
    }

    @Test
    fun `create and cache`() {
        `when`(sharedPreferencesFactoryMock.create(anyInt())).thenReturn(mock(SharedPreferences::class.java))

        //create with the same id twice
        val settings1 = subject.create(0)
        val settings2 = subject.create(0)

        assertThat(settings1, IsSame(settings2))
        verify(sharedPreferencesFactoryMock, times(1)).create(anyInt())

        //create with another id
        val settings3 = subject.create(1)
        assertThat(settings2, IsNot(IsSame(settings3)))
        verify(sharedPreferencesFactoryMock, times(2)).create(anyInt())
    }
}