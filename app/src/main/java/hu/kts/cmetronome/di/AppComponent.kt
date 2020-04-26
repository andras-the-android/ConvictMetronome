package hu.kts.cmetronome.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): AppComponent
    }

    val workoutComponentFactory: WorkoutComponent.Factory

    companion object {

        private var instance: AppComponent? = null

        @Synchronized
        fun init(application: Application) {
            if (instance != null) throw IllegalStateException("AppComponent already initialized")
            instance = DaggerAppComponent.factory().create(application)
        }

        fun get(): AppComponent = instance!!
    }
}