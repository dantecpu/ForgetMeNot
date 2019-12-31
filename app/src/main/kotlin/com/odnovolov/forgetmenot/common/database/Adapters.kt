package com.odnovolov.forgetmenot.common.database

import com.odnovolov.forgetmenot.common.entity.NameCheckResult
import com.odnovolov.forgetmenot.screen.home.adddeck.Stage
import com.odnovolov.forgetmenot.common.entity.PresetNameInputDialogStatus
import com.odnovolov.forgetmenot.common.entity.TestMethod
import com.squareup.sqldelight.ColumnAdapter
import java.util.*

val localeAdapter = object : ColumnAdapter<Locale, String> {
    override fun encode(value: Locale): String {
        return value.toLanguageTag()
    }

    override fun decode(databaseValue: String): Locale {
        return Locale.forLanguageTag(databaseValue)
    }
}

val stageAdapter = object : ColumnAdapter<Stage, String> {
    override fun encode(value: Stage): String {
        return value.name
    }

    override fun decode(databaseValue: String): Stage {
        return try {
            Stage.valueOf(databaseValue)
        } catch (e: IllegalArgumentException) {
            Stage.Idle
        }
    }
}

val listOfLocalesAdapter = object : ColumnAdapter<List<Locale>, String?> {
    override fun encode(value: List<Locale>): String? {
        return if (value.isEmpty()) null
        else value.joinToString(
            separator = ",",
            transform = localeAdapter::encode
        )
    }

    override fun decode(databaseValue: String?): List<Locale> {
        return databaseValue?.split(",")?.map(localeAdapter::decode) ?: emptyList()
    }
}

val nameCheckResultAdapter = object : ColumnAdapter<NameCheckResult, String> {
    override fun encode(value: NameCheckResult): String = value.name

    override fun decode(databaseValue: String): NameCheckResult =
        NameCheckResult.valueOf(databaseValue)
}

val presetNameInputDialogStatusAdapter =
    object : ColumnAdapter<PresetNameInputDialogStatus, String> {
        override fun encode(value: PresetNameInputDialogStatus): String = value.name

        override fun decode(databaseValue: String): PresetNameInputDialogStatus =
            PresetNameInputDialogStatus.valueOf(databaseValue)
    }

val testMethodAdapter = object : ColumnAdapter<TestMethod, String> {
    override fun encode(value: TestMethod): String {
        return value.name
    }

    override fun decode(databaseValue: String): TestMethod {
        return TestMethod.valueOf(databaseValue)
    }
}

fun Long.asBoolean() = this == 1L