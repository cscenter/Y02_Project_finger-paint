package ru.cscenter.fingerpaint.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.GameType
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.dateToString
import ru.cscenter.fingerpaint.models.StatisticsModel

fun navigateToStatistics(fromFragment: Fragment, user: User) {
    val navController = fromFragment.findNavController()
    val statisticsModel: StatisticsModel by fromFragment.activityViewModels()
    statisticsModel.setUser(user)
    navController.navigate(R.id.nav_statistics)
}

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        val statisticsModel: StatisticsModel by activityViewModels()
        val userNameView: TextView = root.findViewById(R.id.user_name)
        val figureChooseChart: BarChart = root.findViewById(R.id.chart1)
        val letterChooseChart: BarChart = root.findViewById(R.id.chart2)
        val figureColorChooseChart: BarChart = root.findViewById(R.id.chart3)
        val letterColorChooseChart: BarChart = root.findViewById(R.id.chart4)
        val drawingChart: BarChart = root.findViewById(R.id.chart5)
        val contouringChart: BarChart = root.findViewById(R.id.chart6)

        statisticsModel.getUser().observe(viewLifecycleOwner, Observer { user ->
            userNameView.text = user.name
        })


        statisticsModel.getUserAllStatistics()
            .observe(viewLifecycleOwner, Observer { allStatistics ->
                val statisticsMap = allStatistics.groupBy { it.type }
                fun getList(type: GameType) = statisticsMap[type] ?: emptyList()

                initChart(
                    getList(GameType.CHOOSE_FIGURE),
                    getString(R.string.choose_figure),
                    figureChooseChart
                )

                initChart(
                    getList(GameType.CHOOSE_LETTER),
                    getString(R.string.choose_letter),
                    letterChooseChart
                )

                initChart(
                    getList(GameType.CHOOSE_FIGURE_COLOR),
                    getString(R.string.choose_figure_color),
                    figureColorChooseChart
                )

                initChart(
                    getList(GameType.CHOOSE_LETTER_COLOR),
                    getString(R.string.choose_letter_color),
                    letterColorChooseChart
                )

                initChart(
                    getList(GameType.DRAW_FIGURE),
                    getString(R.string.drawing_figure),
                    drawingChart
                )

                initChart(
                    getList(GameType.DRAW_LETTER),
                    getString(R.string.contouring),
                    contouringChart
                )
            })

        return root
    }

    private fun getDataPointsAndLabels(
        allStatistics: List<Statistic>
    ): Pair<List<BarEntry>, List<String>> {
        val data = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        for (statistic in allStatistics) {
            val success = statistic.success
            val total = statistic.total
            if (total > 0) { // only days with activity
                data.add(BarEntry(data.size.toFloat(), 100f * success / total))
                labels.add(dateToString(statistic.date))
            }
        }
        return Pair(data, labels)
    }

    private fun initChart(
        allStatistics: List<Statistic>,
        name: String,
        chart: BarChart
    ) {
        val (dataPoints, labels) = getDataPointsAndLabels(allStatistics)
        val barDataSet = BarDataSet(dataPoints, name).apply {
            colors = MainApplication.gameResources.colors.map { color -> color.color }
            axisDependency = YAxis.AxisDependency.LEFT
        }

        chart.apply {
            data = BarData(barDataSet).apply {
                barWidth = BAR_WIDTH
                setValueTextSize(VALUES_TEXT_SIZE)
            }

            description.apply {
                text = name
                textSize = TITLE_TEXT_SIZE
                textColor = Color.BLACK
            }

            axisLeft.apply {
                textSize = AXIS_TEXT_SIZE
                granularity = Y_GRANULARITY
                axisMaximum = Y_MAX
                axisMinimum = Y_MIN
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = AXIS_TEXT_SIZE
                granularity = X_GRANULARITY
                setValueFormatter { value, _ ->
                    labels.getOrElse(value.toInt()) { "" }
                }
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            setVisibleXRange(MIN_BARS_VISIBLE, MAX_BARS_VISIBLE)
            moveViewToX(dataPoints.size.toFloat())

            invalidate()
        }
    }

    companion object {
        private const val Y_GRANULARITY = 10f
        private const val Y_MAX = 100f
        private const val Y_MIN = 0f
        private const val VALUES_TEXT_SIZE = 15f
        private const val AXIS_TEXT_SIZE = 10f
        private const val TITLE_TEXT_SIZE = 15f
        private const val X_GRANULARITY = 1f
        private const val BAR_WIDTH = 0.5f
        private const val MIN_BARS_VISIBLE = 2f
        private const val MAX_BARS_VISIBLE = 5f

    }
}
