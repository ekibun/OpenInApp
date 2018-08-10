package soko.ekibun.openinapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import soko.ekibun.openinapp.wechat.OpenWechatActivity


class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.action){
            Intent.ACTION_SEND->{
                OpenWechatActivity.openInWechat(this, intent.getStringExtra(Intent.EXTRA_TEXT))
            }
        }
        finish()
    }


}
