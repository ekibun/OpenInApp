package soko.ekibun.openinapp.wechat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import soko.ekibun.openinapp.R
import soko.ekibun.openinapp.util.PreferencesUtil

class OpenWechatActivity : AppCompatActivity() {

    val openUrl by lazy {
        if(intent.action == Intent.ACTION_VIEW)
            intent?.data.toString()
        else
            intent.getStringExtra(EXTRA_URL)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openInWechat()
    }

    private var flagRequest = false
    private var flagAccessibility = false
    private var flagStorage = false
    private fun checkPermission(): Boolean {
        if (!PreferencesUtil.isAccessibilitySettingsOn(this)) {
            if(!flagAccessibility) {
                flagRequest = true
                startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQUEST_ACCESS_CODE) }
            flagAccessibility = true
            return false
        }
        if (Build.VERSION.SDK_INT >= 23 && !PreferencesUtil.isStorageEnable(this)) {
            if(!flagStorage) {
                flagStorage = true
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_CODE) }
            flagRequest = true
            return false
        }
        return true
    }

    private fun openInWechat(url: String = openUrl){
        if(checkPermission()){
            WechatAccessibilityService.openInWechat(this, url)
            this.finish()
        }else{
            Toast.makeText(this, R.string.toast_no_permit, Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        flagRequest = false
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACCESS_CODE)
            openInWechat()
        if (!flagRequest)
            this.finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        flagRequest = false
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_CODE)
            openInWechat()
        if (!flagRequest)
            this.finish()
    }

    override fun onStop() {
        super.onStop()
        if(!flagRequest)
            this.finish()
    }

    companion object {

        private const val REQUEST_ACCESS_CODE = 0
        private const val REQUEST_STORAGE_CODE = 1
        private const val EXTRA_URL = "extraUrl"
        fun openInWechat(context: Context, url: String){
            val intent = Intent(context, OpenWechatActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            context.startActivity(intent)
        }
    }
}
