package com.example.stickerkb
import android.content.Context
import android.graphics.*
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import kotlin.math.max

object Store {
    fun dir(c: Context) = File(c.filesDir, "stickers").apply { mkdirs() }
    fun list(c: Context): List<File> =
        dir(c).listFiles { f -> f.extension == "png" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    fun save(c: Context, b: Bitmap): File {
        val f = File(dir(c), "s_${System.currentTimeMillis()}.png")
        f.outputStream().use { b.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return f
    }
    /** Đưa ảnh về khung 512x512 trong suốt, giữ tỉ lệ. */
    fun fit(b: Bitmap): Bitmap {
        val s = 512f / max(b.width, b.height)
        val w = (b.width * s).toInt(); val h = (b.height * s).toInt()
        val out = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        Canvas(out).drawBitmap(Bitmap.createScaledBitmap(b, w, h, true), (512 - w) / 2f, (512 - h) / 2f, null)
        return out
    }
}

class StickerAdapter(private val ctx: Context, val files: List<File>, private val sizePx: Int) : BaseAdapter() {
    private val cache = LruCache<String, Bitmap>(60)
    override fun getCount() = files.size
    override fun getItem(i: Int) = files[i]
    override fun getItemId(i: Int) = i.toLong()
    override fun getView(i: Int, v: View?, p: ViewGroup?): View {
        val iv = (v as? ImageView) ?: ImageView(ctx).apply {
            layoutParams = AbsListView.LayoutParams(sizePx, sizePx); setPadding(8, 8, 8, 8)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val f = files[i]
        val bmp = cache.get(f.path) ?: BitmapFactory.decodeFile(f.path, BitmapFactory.Options().apply { inSampleSize = 2 })
            ?.also { cache.put(f.path, it) }
        iv.setImageBitmap(bmp)
        return iv
    }
}
