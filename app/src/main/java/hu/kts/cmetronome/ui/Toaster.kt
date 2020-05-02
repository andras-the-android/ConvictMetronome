package hu.kts.cmetronome.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import hu.kts.cmetronome.di.AppContext
import javax.inject.Inject

class Toaster @Inject constructor(@AppContext private val context: Context) {

    fun showShort(@StringRes resId: Int) = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()

}