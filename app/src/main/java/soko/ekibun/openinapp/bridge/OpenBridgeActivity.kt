package soko.ekibun.openinapp.bridge

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Parcelable

class OpenBridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.component = null
        intent.`package` = null
        startActivity(chooseExcludeSelf(intent))
        finish()
    }

    private fun chooseExcludeSelf(intent: Intent): Intent? {
        val targetedShareIntents = ArrayList<Intent>()
        val resInfo = packageManager.queryIntentActivities(intent, 0)
        if (!resInfo.isEmpty()) {
            for (info in resInfo) {
                val targetedShare = Intent(intent)
                if (!info.activityInfo.packageName.equals(packageName, ignoreCase = true)) {
                    targetedShare.setPackage(info.activityInfo.packageName)
                    targetedShareIntents.add(targetedShare)
                }
            }
            val chooserIntent = Intent.createChooser(targetedShareIntents.removeAt(0),
                    "打开")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    targetedShareIntents.toTypedArray<Parcelable>())
            return chooserIntent
        }
        return intent
    }
}
