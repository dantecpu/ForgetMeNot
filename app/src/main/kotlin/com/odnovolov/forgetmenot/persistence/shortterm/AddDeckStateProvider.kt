package com.odnovolov.forgetmenot.persistence.shortterm

import com.odnovolov.forgetmenot.domain.interactor.deckadder.CardPrototype
import com.odnovolov.forgetmenot.domain.interactor.deckadder.DeckAdder
import com.odnovolov.forgetmenot.domain.interactor.deckadder.Stage
import com.odnovolov.forgetmenot.persistence.shortterm.AddDeckStateProvider.SerializableAddDeckState
import kotlinx.serialization.Serializable

class AddDeckStateProvider(
    override val serializableId: String = DeckAdder.State::class.simpleName!!,
    override val defaultState: DeckAdder.State? = null
) : BaseSerializableStateProvider<DeckAdder.State, SerializableAddDeckState>() {
    @Serializable
    data class SerializableAddDeckState(
        val stage: Stage,
        val cardPrototypes: List<CardPrototype>?
    )

    override val serializer = SerializableAddDeckState.serializer()

    override fun toSerializable(state: DeckAdder.State) = SerializableAddDeckState(
        state.stage,
        state.cardPrototypes
    )

    override fun toOriginal(serializableState: SerializableAddDeckState) = DeckAdder.State().apply {
        stage = serializableState.stage
        cardPrototypes = serializableState.cardPrototypes
    }
}