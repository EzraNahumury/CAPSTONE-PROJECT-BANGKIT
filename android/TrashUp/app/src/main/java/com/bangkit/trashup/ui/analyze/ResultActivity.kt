package com.bangkit.trashup.ui.analyze

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.trashup.R
import com.bangkit.trashup.databinding.ActivityResultBinding
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Hasil"

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (!imageUri.isNullOrEmpty()) {
            Log.d("Image URI", "Displaying image: $imageUri")

            Glide.with(this)
                .load(Uri.parse(imageUri))
                .placeholder(R.drawable.baseline_hide_image_op_24)
                .into(binding.resultImage)
        } else {
            Toast.makeText(this, "Gambar tidak tersedia", Toast.LENGTH_SHORT).show()
        }

        // Mengambil hasil teks dari Intent
        val result = intent.getStringExtra(EXTRA_RESULT)
        if (!result.isNullOrEmpty()) {
            Log.d("Result", "Displaying result: $result")

            // Format teks untuk menampilkan hasil lebih baik
            val formattedText = result
                .replace("(?<!\\d)(?<=\\S)([.!?])(?=\\s|$)".toRegex(), "$1<br>")
                .replace(":", ":<br>")
                .replace("(?m)^.*?:".toRegex(), "<br>$0")
                .replace(",", ",<br>-")
                .replace("(?m)^##\\s*(.+)".toRegex(), "<h2>$1</h2>")

            // Menampilkan teks hasil ke `TextView`
            binding.resultText.text = Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.resultText.text = "Hasil tidak tersedia"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}
