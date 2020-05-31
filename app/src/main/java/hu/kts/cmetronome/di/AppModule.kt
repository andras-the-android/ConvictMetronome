package hu.kts.cmetronome.di

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import hu.kts.cmetronome.TimeProvider

@Module
object AppModule {

    @Provides
    @AppContext
    @AppScope
    fun appContext(application: Application): Context = application

    @Provides
    @TimeProviderRep
    @AppScope
    fun timeProviderRep() = TimeProvider(500)

    @Provides
    @TimeProviderCountdowner
    @AppScope
    fun timeProviderCountdowner() = TimeProvider(1000)

    @Provides
    @TimeProviderStopwatch
    @AppScope
    fun timeProviderStopwatch() = TimeProvider(1000)

    @Provides
    @AppScope
    fun sharedPreferences(application: Application) = PreferenceManager.getDefaultSharedPreferences(application)
}