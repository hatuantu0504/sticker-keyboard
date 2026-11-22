package com.example.stickerkb
import android.app.AlertDialog
import android.os.Bundle
import android.widget.GridView
import androidx.activity.ComponentActivity

class LibraryActivity : ComponentActivity() {
    private lateinit var grid: GridView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        grid = GridView(this).apply { numColumns = 3 }
        setContentView(grid)
        grid.setOnItemLongClickListener { _, _, i, _ ->
            val f = (grid.adapter as StickerAdapter).files[i]
            AlertDialog.Builder(this).setMessage("Xoá sticker này?")
                .setPositiveButton("Xoá") { _, _ -> f.delete(); refresh() }
                .setNegativeButton("Huỷ", null).show()
            true
        }
    }
    override fun onResume() { super.onResume(); refresh() }
    private fun refresh() {
        grid.adapter = StickerAdapter(this, Store.list(this), resources.displayMetrics.widthPixels / 3)
    }
}
