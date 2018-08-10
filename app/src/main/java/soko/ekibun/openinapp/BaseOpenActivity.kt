package soko.ekibun.openinapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseOpenActivity: AppCompatActivity(){
    val url: String by lazy {
        if(intent.action == Intent.ACTION_VIEW)
            intent?.data.toString()
        else{
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            Regex("""(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]""").find(sharedText)?.groupValues?.get(0)?:sharedText
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openUrl()
    }

    abstract fun openUrl()
}