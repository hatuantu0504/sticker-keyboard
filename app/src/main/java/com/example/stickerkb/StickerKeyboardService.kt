package com.example.stickerkb
import android.content.ClipDescription
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import android.view.ViewGroup.LayoutParams.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import java.io.File

class StickerKeyboardService : InputMethodService() {
    private lateinit var grid: GridView

    override fun onCreateInputView(): View {
        val d = resources.displayMetrics.density
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(0xFFF2F2F2.toInt()) }
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        bar.addView(Button(this).apply {
            text = "🌐 Đổi bàn phím"
            setOnClickListener { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        })
        grid = GridView(this).apply {
            numColumns = 4
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, (260 * d).toInt())
            setOnItemClickListener { _, _, i, _ -> send((adapter as StickerAdapter).files[i]) }
        }
        root.addView(bar); root.addView(grid)
        return root
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        grid.adapter = StickerAdapter(this, Store.list(this), resources.displayMetrics.widthPixels / 4)
    }

    private fun send(f: File) {
        val ei = currentInputEditorInfo ?: return
        val ic = currentInputConnection ?: return
        val ok = EditorInfoCompat.getContentMimeTypes(ei).any { ClipDescription.compareMimeTypes("image/png", it) }
        if (!ok) return Toast.makeText(this, "Ô nhập này không nhận sticker từ bàn phím", Toast.LENGTH_SHORT).show()
        val uri = FileProvider.getUriForFile(this, "$packageName.files", f)
        val info = InputContentInfoCompat(uri, ClipDescription("sticker", arrayOf("image/png")), null)
        val flags = if (Build.VERSION.SDK_INT >= 25) InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION else 0
        InputConnectionCompat.commitContent(ic, ei, info, flags, null)
    }
}
