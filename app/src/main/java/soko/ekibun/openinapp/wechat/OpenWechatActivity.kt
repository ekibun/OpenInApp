package soko.ekibun.openinapp.wechat

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import soko.ekibun.openinapp.BaseOpenActivity
import soko.ekibun.openinapp.R
import soko.ekibun.openinapp.util.PreferencesUtil

class OpenWechatActivity : BaseOpenActivity() {
    override fun openUrl() {
        if(checkPermission()){
            WechatAccessibilityService.openInWechat(this, url)
            this.finish()
        }else{
            Toast.makeText(this, R.string.toast_no_permit, Toast.LENGTH_SHORT).show()
        }
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        flagRequest = false
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACCESS_CODE)
            openUrl()
        if (!flagRequest)
            this.finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        flagRequest = false
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_CODE)
            openUrl()
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
    }
}
