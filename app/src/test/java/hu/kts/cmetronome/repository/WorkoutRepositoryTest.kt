package hu.kts.cmetronome.repository


import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test


class WorkoutRepositoryTest {

    lateinit var subject: WorkoutRepository

    @Before
    fun initSubject() {
        subject = WorkoutRepository()
    }

    @Test
    fun `rep counter`() {
        subject.increaseRepCounter()
        subject.increaseRepCounter()
        subject.increaseRepCounter()

        assertThat(subject.repCount, equalTo(3))
    }

    @Test
    fun `set counter`() {
        subject.increaseSetCounter()
        subject.increaseSetCounter()
        subject.increaseSetCounter()

        assertThat(subject.setCount, equalTo(3))
    }

    @Test
    fun `reset rep counter`() {
        subject.increaseRepCounter()
        subject.resetRepCounter()

        assertThat(subject.repCount, equalTo(0))
    }

    @Test
    fun `reset all counters`() {
        subject.increaseRepCounter()
        subject.increaseSetCounter()
        subject.stopwatchStartTime = 123L
        subject.resetCounters()

        assertThat(subject.repCount, equalTo(0))
        assertThat(subject.setCount, equalTo(0))
        assertThat(subject.stopwatchStartTime, equalTo(0L))
    }



}