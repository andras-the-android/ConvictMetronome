package hu.kts.cmetronome.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class AppContext

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeProviderRep

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeProviderStopwatch

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeProviderCountdowner