package hu.kts.cmetronome.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.kts.cmetronome.admob.AdViewWrapper
import hu.kts.cmetronome.databinding.FragmentWorkoutBinding
import hu.kts.cmetronome.di.Injector


class WorkoutFragment: Fragment() {

    lateinit var workoutController: WorkoutController
    lateinit var whatsNew: WhatsNew
    lateinit var binding: FragmentWorkoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWorkoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Injector.inject(this)

        lifecycle.addObserver(AdViewWrapper(binding.adView))
        lifecycle.addObserver(workoutController)

        binding.repCounterTextView.setOnClickListener { workoutController.onRepCounterClick() }
        binding.repCounterTextView.setOnLongClickListener { workoutController.onRepCounterLongClick() }

        whatsNew.run()
    }
}