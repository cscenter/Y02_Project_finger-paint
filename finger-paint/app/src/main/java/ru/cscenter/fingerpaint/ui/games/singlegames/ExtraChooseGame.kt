package ru.cscenter.fingerpaint.ui.games.singlegames

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.api.ApiChooseTask
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.network.FingerPaintApi
import ru.cscenter.fingerpaint.ui.games.base.BaseGameActivity
import ru.cscenter.fingerpaint.ui.games.base.ChooseGame


class ExtraChooseGame(config: Config, gameActivity: BaseGameActivity) :
    ChooseGame(config, gameActivity) {

    override val gameType: GameType = GameType.CHOOSE_SPECIAL

    companion object {

        private fun loadImageIntoView(view: ImageView, imageId: Long) =
            Glide.with(view).load(FingerPaintApi.pictureUrl(imageId)).placeholder(R.drawable.ic_loading_icon).into(view)

        fun createConfig(
            task: ApiChooseTask
        ) = Config(
            question = task.text,
            correctImageViewSetter = { loadImageIntoView(it, task.correctImageId) },
            incorrectImageViewSetter = { loadImageIntoView(it, task.incorrectImageId) }
        )
    }
}
