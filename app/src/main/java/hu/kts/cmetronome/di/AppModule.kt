package hu.kts.cmetronome.di

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import hu.kts.cmetronome.TimeProvider
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @AppContext
    @Singleton
    fun appContext(application: Application): Context = application

    @Provides
    @TimeProviderRep
    @Singleton
    fun timeProviderRep() = TimeProvider(500)

    @Provides
    @TimeProviderCountdowner
    @Singleton
    fun timeProviderCountdowner() = TimeProvider(1000)

    @Provides
    @TimeProviderStopwatch
    @Singleton
    fun timeProviderStopwatch() = TimeProvider(1000)

    @Provides
    @Singleton
    fun sharedPreferences(application: Application) = PreferenceManager.getDefaultSharedPreferences(application)
}