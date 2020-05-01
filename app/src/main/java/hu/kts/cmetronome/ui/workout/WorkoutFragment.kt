package hu.kts.cmetronome.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.kts.cmetronome.admob.AdViewWrapper
import hu.kts.cmetronome.databinding.FragmentWorkoutBinding
import hu.kts.cmetronome.di.AppComponent
import javax.inject.Inject


class WorkoutFragment: Fragment() {

    @Inject
    lateinit var workoutController: WorkoutController
    @Inject
    lateinit var whatsNew: WhatsNew

    lateinit var binding: FragmentWorkoutBinding

    private var injected = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            binding.root
        } catch (e: UninitializedPropertyAccessException) {
            binding = FragmentWorkoutBinding.inflate(layoutInflater, container, false)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!injected) {
            AppComponent.get().workoutComponentFactory.create(this).inject(this)
            injected = true
        }

        lifecycle.addObserver(AdViewWrapper(binding.adView))
        lifecycle.addObserver(workoutController)

        binding.repCounterTextView.setOnClickListener { workoutController.onRepCounterClick() }
        binding.repCounterTextView.setOnLongClickListener { workoutController.onRepCounterLongClick() }

        whatsNew.run()
    }
}