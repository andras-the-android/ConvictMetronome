package hu.kts.cmetronome

import android.content.SharedPreferences
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.stubbing.Answer

fun SharedPreferences.createMockEditor(): SharedPreferences.Editor {
    val editorMock = Mockito.mock(SharedPreferences.Editor::class.java, Answer { invocation ->
        if (invocation.method.returnType == SharedPreferences.Editor::class.java) {
            return@Answer invocation.mock
        } else {
            return@Answer RETURNS_DEFAULTS.answer(invocation)
        }

    })
    Mockito.`when`(edit()).thenReturn(editorMock)
    return editorMock
}