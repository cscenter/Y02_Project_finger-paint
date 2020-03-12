package ru.cscenter.fingerpaint.ui.games

import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.Game
import ru.cscenter.fingerpaint.ui.games.singlegames.ExtraChooseGame

class ExtraChooseActivity : BaseGameActivity() {
    override fun configureGames(): List<Game> {
        @Suppress("UNCHECKED_CAST")
        val tasks = intent?.extras?.getSerializable(ARG_CHOOSE_TASK) as Array<ApiChooseTask>
        return tasks
            .map { task -> ExtraChooseGame.createConfig(task) }
            .map { config -> ExtraChooseGame(config, this) }
    }

    companion object {
        const val ARG_CHOOSE_TASK = "arg-choose-task"
    }
}
