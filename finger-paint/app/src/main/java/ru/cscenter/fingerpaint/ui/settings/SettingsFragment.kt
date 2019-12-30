package ru.cscenter.fingerpaint.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.service.MyVibrator

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val vibrateSwitch: Switch = root.findViewById(R.id.vibrate_switch)
        vibrateSwitch.isChecked = MainApplication.settings.getVibrate()
        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            MainApplication.settings.setVibrate(isChecked)
            if (isChecked) {
                MyVibrator.vibrate(context!!, MyVibrator.LENGTH_SHORT)
            }
        }

        return root
    }
}
