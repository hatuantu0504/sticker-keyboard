package com.example.stickerkb
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.*
import android.widget.*
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(48, 48, 48, 48) }
        fun btn(t: String, f: () -> Unit) = Button(this).apply {
            text = t; textSize = 20f; setOnClickListener { f() }
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { bottomMargin = 32 }
        }
        root.addView(btn("🎨 Tạo sticker") { startActivity(Intent(this, CreateActivity::class.java)) })
        root.addView(btn("📁 Lưu sticker") { startActivity(Intent(this, LibraryActivity::class.java)) })
        root.addView(btn("⌨️ Bật bàn phím Sticker") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) })
        setContentView(root)
    }
}
