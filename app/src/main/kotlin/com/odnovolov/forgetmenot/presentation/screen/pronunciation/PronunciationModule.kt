package com.odnovolov.forgetmenot.presentation.screen.pronunciation

import com.odnovolov.forgetmenot.domain.interactor.decksettings.PronunciationSettings
import com.odnovolov.forgetmenot.presentation.common.SpeakerImpl
import com.odnovolov.forgetmenot.presentation.common.Store
import com.odnovolov.forgetmenot.presentation.screen.decksettings.DECK_SETTINGS_SCOPED_ID
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.onClose

val pronunciationModule = module {
    scope<PronunciationViewModel> {
        scoped {
            PronunciationSettings(
                deckSettings = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                globalState = get()
            )
        }
        scoped { get<Store>().loadPronunciationScreenState() }
        scoped { SpeakerImpl(applicationContext = get()) } onClose { it?.shutdown() }
        scoped {
            PronunciationController(
                deckSettingsState = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                pronunciationSettings = get(),
                pronunciationScreenState = get(),
                globalState = get(),
                store = get()
            )
        } onClose { it?.onCleared() }
        viewModel {
            PronunciationViewModel(
                deckSettingsState = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                pronunciationScreenState = get(),
                speakerImpl = get(),
                globalState = get()
            )
        }
    }
}

const val PRONUNCIATION_SCOPE_ID = "PRONUNCIATION_SCOPE_ID"