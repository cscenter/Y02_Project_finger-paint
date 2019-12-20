package ru.cscenter.fingerpaint.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.db.Statistic
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.dateToString

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)

        val dbController = MainApplication.dbController
        var user: User? = null
        arguments?.let {
            val safeArgs = StatisticsFragmentArgs.fromBundle(it)
            val userId = safeArgs.userId
            user = dbController.getUser(userId)
        }

        if (user == null) {
            Log.e("FingerPaint.Statistics", " Illegal arguments provided.")
            onDestroy()
            return null
        }

        val userNameView: TextView = root.findViewById(R.id.user_name)
        userNameView.text = user!!.name

        val figureChooseChart: BarChart = root.findViewById(R.id.chart1)
        val letterChooseChart: BarChart = root.findViewById(R.id.chart2)
        val figureColorChooseChart: BarChart = root.findViewById(R.id.chart3)
        val letterColorChooseChart: BarChart = root.findViewById(R.id.chart4)
        val drawingChart: BarChart = root.findViewById(R.id.chart5)
        val contouringChart: BarChart = root.findViewById(R.id.chart6)

        val allStatistics = dbController.getUserAllStatistics(user!!.id)

        initChart(
            allStatistics,
            { st -> Pair(st.figureChooseSuccess, st.figureChooseTotal) },
            "Choose figure",
            figureChooseChart
        )

        initChart(
            allStatistics,
            { st -> Pair(st.letterChooseSuccess, st.letterChooseTotal) },
            "Choose letter",
            letterChooseChart
        )

        initChart(
            allStatistics,
            { st -> Pair(st.figureColorChooseSuccess, st.figureColorChooseTotal) },
            "Choose figure color",
            figureColorChooseChart
        )

        initChart(
            allStatistics,
            { st -> Pair(st.letterColorChooseSuccess, st.letterColorChooseTotal) },
            "Choose letter color",
            letterColorChooseChart
        )

        initChart(
            allStatistics,
            { st -> Pair(st.drawingSuccess, st.drawingTotal) },
            "Drawing figure",
            drawingChart
        )

        initChart(
            allStatistics,
            { st -> Pair(st.contouringSuccess, st.contouringTotal) },
            "Contouring",
            contouringChart
        )

        return root
    }

    private fun getDataPointsAndLabels(
        allStatistics: List<Statistic>,
        getter: (Statistic) -> Pair<Int, Int>
    ): Pair<List<BarEntry>, List<String>> {
        val data = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        for (statistic in allStatistics) {
            val (success, total) = getter(statistic)
            if (total > 0) { // only days with activity
                data.add(BarEntry(data.size.toFloat(), 100f * success / total))
                labels.add(dateToString(statistic.date))
            }
        }
        return Pair(data, labels)
    }

    private fun initChart(
        allStatistics: List<Statistic>,
        getter: (Statistic) -> Pair<Int, Int>,
        name: String,
        chart: BarChart
    ) {
        val (dataPoints, labels) = getDataPointsAndLabels(allStatistics, getter)
        val barDataSet = BarDataSet(dataPoints, name).apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
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
