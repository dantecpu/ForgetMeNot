package com.odnovolov.forgetmenot.presentation.screen.fileimport.cardsfile.sourcetext

import com.odnovolov.forgetmenot.domain.interactor.fileimport.CardsFile
import com.odnovolov.forgetmenot.domain.interactor.fileimport.FileImporter
import com.odnovolov.forgetmenot.domain.interactor.fileimport.Parser.ErrorBlock
import com.odnovolov.forgetmenot.presentation.common.businessLogicThread
import com.odnovolov.forgetmenot.presentation.screen.fileimport.CharsetItem
import kotlinx.coroutines.flow.*
import java.nio.charset.Charset

class ImportedTextEditorViewModel(
    private val cardsFileId: Long,
    private val fileImporterState: FileImporter.State
) {
    private val cardsFile: Flow<CardsFile> =
        fileImporterState.flowOf(FileImporter.State::files)
            .map { files: List<CardsFile> ->
                files.find { file: CardsFile -> file.id == cardsFileId }
            }
            .filterNotNull()

    val currentCharset: Flow<Charset> = cardsFile.flatMapLatest { cardsFile: CardsFile ->
        cardsFile.flowOf(CardsFile::charset)
    }

    @OptIn(ExperimentalStdlibApi::class)
    val availableCharsets: Flow<List<CharsetItem>> = currentCharset.map { currentCharset: Charset ->
        buildList<Charset> {
            addAll(mainCharsets)
            for (charset in Charset.availableCharsets().values) {
                if (charset !in mainCharsets) {
                    add(charset)
                }
            }
        }.map { charset: Charset ->
            CharsetItem(
                charset,
                isSelected = charset == currentCharset
            )
        }
    }

    val sourceTextWithNewEncoding: Flow<String> = currentCharset.mapNotNull {
        fileImporterState.files.find { file: CardsFile -> file.id == cardsFileId }?.text
    }

    val errors: Flow<List<ErrorBlock>> = cardsFile.flatMapLatest { cardsFile: CardsFile ->
        cardsFile.flowOf(CardsFile::errors)
    }
        .flowOn(businessLogicThread)
}