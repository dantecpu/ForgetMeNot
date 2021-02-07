package com.odnovolov.forgetmenot.persistence.shortterm

import com.odnovolov.forgetmenot.Database
import com.odnovolov.forgetmenot.domain.entity.*
import com.odnovolov.forgetmenot.domain.interactor.fileimport.CardsFile
import com.odnovolov.forgetmenot.domain.interactor.fileimport.FileImporter
import com.odnovolov.forgetmenot.domain.interactor.fileimport.FmnFormatParser
import com.odnovolov.forgetmenot.domain.interactor.fileimport.Parser
import com.odnovolov.forgetmenot.persistence.shortterm.FileImporterStateProvider.SerializableState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class FileImporterStateProvider(
    json: Json,
    database: Database,
    private val globalState: GlobalState,
    override val key: String = FileImporter.State::class.qualifiedName!!
) : BaseSerializableStateProvider<FileImporter.State, SerializableState>(
    json,
    database
) {
    @Serializable
    data class SerializableState(
        val files: List<SerializableCardsFile>,
        val currentPosition: Int
    )

    override val serializer = SerializableState.serializer()

    override fun toSerializable(state: FileImporter.State): SerializableState {
        val serializableCardsFiles: List<SerializableCardsFile> =
            state.files.map { cardsFile: CardsFile ->
                val serializableAbstractDeck: SerializableAbstractDeck =
                    when (val deckWhereToAdd = cardsFile.deckWhereToAdd) {
                        is NewDeck -> SerializableNewDeck(deckWhereToAdd.deckName)
                        is ExistingDeck -> SerializableExistingDeck(deckWhereToAdd.deck.id)
                        else -> error(ERROR_MESSAGE_UNKNOWN_IMPLEMENTATION_OF_ABSTRACT_DECK)
                    }
                SerializableCardsFile(
                    cardsFile.sourceBytes,
                    cardsFile.charset.name(),
                    serializableAbstractDeck,
                    cardsFile.parser.state.text
                )
            }
        return SerializableState(serializableCardsFiles, state.currentPosition)
    }

    override fun toOriginal(serializableState: SerializableState): FileImporter.State {
        val cardsFiles: List<CardsFile> = serializableState.files
            .map { serializableCardsFile: SerializableCardsFile ->
                val deckWhereToAdd: AbstractDeck =
                    when (val file = serializableCardsFile.serializableAbstractDeck) {
                    is SerializableNewDeck -> NewDeck(file.deckName)
                    is SerializableExistingDeck -> {
                        val deck: Deck = globalState.decks.first { it.id == file.deckId }
                        ExistingDeck(deck)
                    }
                }
                val charset = Charset.forName(serializableCardsFile.charsetName)
                val parserState = Parser.State(
                    text = serializableCardsFile.text,
                    cardPrototypes = emptyList(),
                    errorLines = emptyList()
                )
                val parser = FmnFormatParser(parserState)
                CardsFile(
                    serializableCardsFile.sourceBytes,
                    charset,
                    deckWhereToAdd,
                    parser
                )
            }
        return FileImporter.State(cardsFiles, serializableState.currentPosition)
    }
}

@Serializable
data class SerializableCardsFile(
    val sourceBytes: ByteArray,
    val charsetName: String,
    val serializableAbstractDeck: SerializableAbstractDeck,
    val text: String
)

@Serializable
sealed class SerializableAbstractDeck

@Serializable
class SerializableNewDeck(val deckName: String) : SerializableAbstractDeck()

@Serializable
class SerializableExistingDeck(val deckId: Long) : SerializableAbstractDeck()