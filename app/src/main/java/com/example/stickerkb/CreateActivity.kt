package com.example.stickerkb
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlin.math.max

class CreateActivity : ComponentActivity() {
    private var src: Bitmap? = null
    private lateinit var preview: ImageView
    private lateinit var sw: Switch
    private val pick = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { src = load(it); preview.setImageBitmap(src) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 32, 32, 32) }
        preview = ImageView(this).apply {
            setBackgroundColor(0xFFDDDDDD.toInt()); scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f)
        }
        sw = Switch(this).apply { text = "Xoá phông nền (tắt = giữ nguyên)"; isChecked = true; setPadding(0, 24, 0, 24) }
        val choose = Button(this).apply { text = "Chọn ảnh"; setOnClickListener { pick.launch("image/*") } }
        val make = Button(this).apply { text = "Tạo & lưu sticker"; setOnClickListener { make() } }
        listOf(preview, sw, choose, make).forEach { root.addView(it) }
        setContentView(root)
    }

    private fun load(uri: Uri): Bitmap? {
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o) }
        var s = 1
        while (max(o.outWidth, o.outHeight) / s > 1024) s *= 2
        val o2 = BitmapFactory.Options().apply { inSampleSize = s }
        return contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o2) }
    }

    private fun make() {
        val b = src ?: return Toast.makeText(this, "Hãy chọn ảnh trước", Toast.LENGTH_SHORT).show()
        if (!sw.isChecked) return done(b)
        Toast.makeText(this, "Đang xoá phông...", Toast.LENGTH_SHORT).show()
        val opts = SubjectSegmenterOptions.Builder().enableForegroundBitmap().build()
        SubjectSegmentation.getClient(opts).process(InputImage.fromBitmap(b, 0))
            .addOnSuccessListener { done(it.foregroundBitmap ?: b) }
            .addOnFailureListener { Toast.makeText(this, "Lỗi xoá phông: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun done(b: Bitmap) {
        val s = Store.fit(b)
        Store.save(this, s)
        preview.setImageBitmap(s)
        Toast.makeText(this, "Đã lưu vào danh sách sticker", Toast.LENGTH_SHORT).show()
    }
}
