package com.odnovolov.forgetmenot.presentation.screen.repetition.view

import com.odnovolov.forgetmenot.domain.interactor.repetition.Repetition
import com.odnovolov.forgetmenot.presentation.common.LongTermStateSaver
import com.odnovolov.forgetmenot.presentation.common.ShortTermStateProvider
import com.odnovolov.forgetmenot.presentation.common.base.BaseController
import com.odnovolov.forgetmenot.presentation.screen.repetition.view.RepetitionFragmentEvent.*
import com.odnovolov.forgetmenot.presentation.screen.repetition.view.RepetitionViewController.Command
import com.odnovolov.forgetmenot.presentation.screen.repetition.view.RepetitionViewController.Command.SetViewPagerPosition
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RepetitionViewController(
    private val repetition: Repetition,
    private val longTermStateSaver: LongTermStateSaver,
    private val repetitionStateProvider: ShortTermStateProvider<Repetition.State>
) : BaseController<RepetitionFragmentEvent, Command>() {
    sealed class Command {
        class SetViewPagerPosition(val position: Int) : Command()
    }

    init {
        combineTransform(
            repetition.state.flowOf(Repetition.State::repetitionCardPosition),
            repetition.state.flowOf(Repetition.State::isPlaying)
        ) { position: Int, isPlaying: Boolean ->
            if (isPlaying) {
                emit(SetViewPagerPosition(position))
            }
        }
            .onEach { sendCommand(it) }
            .launchIn(coroutineScope)
    }

    override fun handle(event: RepetitionFragmentEvent) {
        when (event) {
            is NewPageBecameSelected -> {
                repetition.setRepetitionCardPosition(event.position)
            }

            ShowAnswerButtonClicked -> {
                repetition.showAnswer()
            }

            PauseButtonClicked -> {
                repetition.pause()
            }

            ResumeButtonClicked -> {
                repetition.resume()
            }
        }
    }

    override fun saveState() {
        longTermStateSaver.saveStateByRegistry()
        repetitionStateProvider.save(repetition.state)
    }
}