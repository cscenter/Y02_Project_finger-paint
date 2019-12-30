package ru.cscenter.fingerpaint.ui.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.R

class RecommendationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recommendations, container, false)

        val recommendationsView: WebView = root.findViewById(R.id.recommendations_view)
        recommendationsView.loadUrl(RECOMMENDATIONS_PATH)
        recommendationsView.settings.defaultTextEncodingName = Charsets.UTF_8.name()

        return root
    }

    companion object {
        private const val RECOMMENDATIONS_PATH = "file:///android_asset/recommendations.html"
    }
}
