package hu.kts.cmetronome.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.kts.cmetronome.R
import hu.kts.cmetronome.admob.AdViewWrapper
import hu.kts.cmetronome.di.Injector
import kotlinx.android.synthetic.main.fragment_workout.*


class WorkoutFragment: Fragment() {

    lateinit var workoutController: WorkoutController
    lateinit var whatsNew: WhatsNew

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Injector.inject(this)

        lifecycle.addObserver(AdViewWrapper(adView))
        lifecycle.addObserver(workoutController)

        repCounterTextView.setOnClickListener { workoutController.onRepCounterClick() }
        repCounterTextView.setOnLongClickListener { workoutController.onRepCounterLongClick() }

        whatsNew.run()
    }
}