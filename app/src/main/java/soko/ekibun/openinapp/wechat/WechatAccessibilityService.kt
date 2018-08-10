package soko.ekibun.openinapp.wechat

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.content.ComponentName
import android.content.Context
import android.graphics.Rect
import soko.ekibun.openinapp.util.FileUtil
import soko.ekibun.openinapp.util.QrCodeUtil
import java.io.File
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.text.TextUtils

class WechatAccessibilityService: AccessibilityService() {

    private var file: File? = null
    var oldTime: Long = 0
    var process = 0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(PROCESSWECHAT)) {
            try {
                val url = intent.getStringExtra("processWechat")
                val qrcode = QrCodeUtil.createQRCode(url, 500)
                oldTime = System.currentTimeMillis()
                file = FileUtil.saveBitmapToCache(this, qrcode, oldTime.toString())
                //FileUtils.requestScanFile(this, file.getPath());
                process = if (openWechatScanUI(this)) 1 else 0
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onInterrupt() {
        //TODO
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if(System.currentTimeMillis() - oldTime > 10000) process = 0
        if(process <= 0) return
        val className = event.className.toString()
        when(process){
            1 ->{
                if ("com.tencent.mm.plugin.scanner.ui.BaseScanUI" != className) return
                val node = findNodeByClass("android.widget.ImageButton", event.source)?:return
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                process++
                oldTime = System.currentTimeMillis()
            }
            2->{
                var node = event.source?.findAccessibilityNodeInfosByText("从相册选取二维码")?.getOrNull(0)?: return
                while(!node.isClickable) node = node.parent
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                process++
                oldTime = System.currentTimeMillis()
            }
            3->{
                if ("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI" != className) return
                findNodeByClass("android.widget.GridView", event.source)?.getChild(1)?.let {
                    it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    process++
                    oldTime = System.currentTimeMillis()
                }
            }
            4->{
                process = 0
                if ("com.tencent.mm.plugin.scanner.ui.BaseScanUI" != className) return
                file?.let{
                    FileUtil.deleteFile(this, it)
                }
                file = null
            }
        }
    }

    private fun findNodeByClass(className: String, root: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        var find: AccessibilityNodeInfo? = null
        if (root != null && root.childCount > 0) {
            for (i in 0 until root.childCount) {
                var child: AccessibilityNodeInfo? = root.getChild(i) ?: continue
                if (TextUtils.equals(className, child!!.className)) {
                    find = child
                    break
                } else {
                    child = findNodeByClass(className, child)
                    if (child == null)
                        continue
                    find = child
                    break
                }
            }
        }
        return find
    }

    private fun enumNodeName(accessibilityNodeInfo: AccessibilityNodeInfo?, pos: Int = 0){
        if(accessibilityNodeInfo == null) return
        for(i in 0 until accessibilityNodeInfo.childCount) {
            val node = accessibilityNodeInfo.getChild(i)?:continue
            val rect = Rect()
            node.getBoundsInScreen(rect)
            Log.v("node", String(CharArray(pos) { ' ' }) + node.text + " " + rect.flattenToString() + node.className + node.contentDescription)
            enumNodeName(node, pos+1)
        }
    }

    companion object {
        const val PROCESSWECHAT = "processWechat"

        fun openInWechat(context: Context, url: String){
            val intent = Intent(context, WechatAccessibilityService::class.java)
            intent.putExtra(PROCESSWECHAT, url)
            context.startService(intent)
        }

        @SuppressLint("WrongConstant")
        fun openWechatScanUI(context: Context): Boolean {
            try {
                val intent = Intent()
                intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                intent.flags = 335544320
                intent.action = "android.intent.action.VIEW"
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }
}