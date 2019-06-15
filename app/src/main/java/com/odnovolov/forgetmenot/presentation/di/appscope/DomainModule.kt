package com.odnovolov.forgetmenot.presentation.di.appscope

import com.odnovolov.forgetmenot.data.repository.DeckRepositoryImpl
import com.odnovolov.forgetmenot.data.repository.ExerciseRepositoryImpl
import com.odnovolov.forgetmenot.domain.feature.addnewdeck.AddNewDeckFeature
import com.odnovolov.forgetmenot.domain.feature.deckspreview.DecksPreviewFeature
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
class DomainModule {

    @AppScope
    @Provides
    fun provideAddNewDeckFeature(repository: DeckRepositoryImpl): AddNewDeckFeature {
        return AddNewDeckFeature(repository, AndroidSchedulers.mainThread())
    }

    @AppScope
    @Provides
    fun provideDecksPreviewFeature(
        deckRepositoryImpl: DeckRepositoryImpl,
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): DecksPreviewFeature {
        return DecksPreviewFeature(
            deckRepositoryImpl,
            exerciseRepositoryImpl,
            AndroidSchedulers.mainThread()
        )
    }

    @AppScope
    @Provides
    fun provideExerciseFeature(exerciseRepository: ExerciseRepositoryImpl): ExerciseFeature {
        return ExerciseFeature(exerciseRepository, AndroidSchedulers.mainThread())
    }
}