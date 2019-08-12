package com.odnovolov.forgetmenot.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.odnovolov.forgetmenot.db.entity.CardDbEntity
import com.odnovolov.forgetmenot.db.entity.DeckDbEntity
import com.odnovolov.forgetmenot.db.entity.PronunciationDbEntity
import com.odnovolov.forgetmenot.entity.Card
import com.odnovolov.forgetmenot.entity.Deck

@Dao
abstract class HomeDao {

    open fun getDecks(): LiveData<List<Deck>> {
        return Transformations.map(getDecksInternal()) { roughDecks: List<RoughDeck> ->
            roughDecks.map { roughDeck: RoughDeck ->
                val cards: List<Card> = roughDeck.cardDbEntities.map { it.toCard() }
                val pronunciation = roughDeck.pronunciationDbEntity?.toPronunciation()
                roughDeck.deckDbEntity.toDeck(cards, pronunciation)
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM decks LEFT JOIN pronunciations ON pronunciation_id_key = pronunciation_id")
    abstract fun getDecksInternal(): LiveData<List<RoughDeck>>

    class RoughDeck {
        @Embedded
        lateinit var deckDbEntity: DeckDbEntity

        @Relation(entity = CardDbEntity::class, entityColumn = "deck_id_fk", parentColumn = "deck_id")
        lateinit var cardDbEntities: List<CardDbEntity>

        @Embedded
        var pronunciationDbEntity: PronunciationDbEntity? = null
    }

    @Query("DELETE FROM decks WHERE deck_id = :deckId")
    abstract fun deleteDeck(deckId: Int): Int

}