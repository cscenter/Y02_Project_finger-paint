package ru.cscenter.fingerpaint.ui.games.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.resources.MyColor

class ColorsAdapter(
    context: Context, private var currentColor: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    private val colors = MainApplication.gameResources.colors
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    inner class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val colorButton: ImageView = view.findViewById(R.id.color_button)

        fun bind(color: MyColor) {
            colorButton.setColorFilter(color.color)
            if (color.color == currentColor) {
                colorButton.scaleY = 1.5f
                colorButton.scaleX = 1.5f
            } else {
                colorButton.scaleY = 1f
                colorButton.scaleX = 1f
            }
            colorButton.setOnClickListener {
                currentColor = color.color
                onClick(color.color)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = layoutInflater.inflate(R.layout.color_item, parent, false)
        return ColorViewHolder(view)
    }

    override fun getItemCount() = colors.size
    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) = holder.bind(colors[position])
}
