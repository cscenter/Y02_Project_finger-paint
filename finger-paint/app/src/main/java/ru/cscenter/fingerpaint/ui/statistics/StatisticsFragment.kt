package ru.cscenter.fingerpaint.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

        val figureChooseChart: BarChart = root.findViewById(R.id.chart1)
        val letterChooseChart: BarChart = root.findViewById(R.id.chart2)
        val colorChooseChart: BarChart = root.findViewById(R.id.chart3)
        val drawingChart: BarChart = root.findViewById(R.id.chart4)
        val contouringChart: BarChart = root.findViewById(R.id.chart5)

        val allStatistics = dbController.getUserAllStatistics(user!!.id)

        setBarData(
            allStatistics,
            { st -> Pair(st.figureChooseSuccess, st.figureChooseTotal) },
            "Choose figure",
            figureChooseChart
        )

        setBarData(
            allStatistics,
            { st -> Pair(st.letterChooseSuccess, st.letterChooseTotal) },
            "Choose letter",
            letterChooseChart
        )

        setBarData(
            allStatistics,
            { st -> Pair(st.colorChooseSuccess, st.colorChooseTotal) },
            "Choose color",
            colorChooseChart
        )

        setBarData(
            allStatistics,
            { st -> Pair(st.drawingSuccess, st.drawingTotal) },
            "Drawing figure",
            drawingChart
        )

        setBarData(
            allStatistics,
            { st -> Pair(st.contouringSuccess, st.contouringTotal) },
            "Contouring",
            contouringChart
        )


        val charts = listOf(
            figureChooseChart, letterChooseChart, colorChooseChart,
            drawingChart, contouringChart
        )
        charts.forEach { it.invalidate() }

        return root
    }

    private fun setBarData(
        allStatistics: List<Statistic>,
        getter: (Statistic) -> Pair<Int, Int>,
        name: String,
        chart: BarChart
    ) {
        val data = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        for (statistic in allStatistics) {
            val (success, total) = getter(statistic)
            if (total > 0) { // only days with activity
                val date = statistic.date
                data.add(BarEntry(data.size.toFloat(), 100f * success / total))
                labels.add(dateToString(date))
            }
        }
        val barDataSet = BarDataSet(data, name)
        chart.data = BarData(barDataSet)

        chart.description.text = name
        chart.xAxis.granularity = 1f
        chart.axisLeft.granularity = 10f
        chart.xAxis.setValueFormatter { value, _ ->
            labels.getOrElse(value.toInt()) { "" }
        }
    }
}
