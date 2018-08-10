package soko.ekibun.openinapp.alipay

import android.content.Intent
import android.net.Uri
import soko.ekibun.openinapp.BaseOpenActivity
import java.net.URISyntaxException

class OpenAlipayActivity : BaseOpenActivity() {
    override fun openUrl() {
        val intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
                "qrcode=" + Uri.encode(url) + "#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
        try { startActivity(Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME))
        } catch (e: URISyntaxException) { e.printStackTrace() }
    }
}
