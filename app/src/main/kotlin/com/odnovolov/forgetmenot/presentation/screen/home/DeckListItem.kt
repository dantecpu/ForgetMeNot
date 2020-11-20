package com.odnovolov.forgetmenot.presentation.screen.home

sealed class DeckListItem {
    object Header : DeckListItem()

    data class DeckPreview(
        val deckId: Long,
        val deckName: String,
        val searchMatchingRanges: List<IntRange>?,
        val averageLaps: String,
        val learnedCount: Int,
        val totalCount: Int,
        val numberOfCardsReadyForExercise: Int?,
        val lastOpenedAt: String
    ) : DeckListItem()

    object Footer : DeckListItem()
}