package com.bangkit.trashup.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bangkit.trashup.R
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bangkit.trashup.databinding.ActivityDetailArticlesBinding
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.ui.favorite.FavoriteViewModel

class DetailArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArticlesBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var favoriteViewModel: FavoriteViewModel

    companion object {
        const val EXTRA_ARTICLES = "extra_articles"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Articles"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ivDetailPhoto = binding.ivDetailPhoto
        val tvDetailTitle = binding.tvDetailTitle
        val tvDetailView = binding.tvDetailView
        val tvDetailWasteType = binding.tvDetailWasteType
        val tvDetailDescription = binding.tvDetailDescription
        val tvDetailTools = binding.tvDetailTools
        val tvDetailSteps = binding.tvDetailSteps
        val favoriteFab = binding.favoriteFab

        val articles: DatasItem? = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_ARTICLES, DatasItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_ARTICLES)
        }

        if (articles != null) {
            // Set up ViewModel and handle favorite logic
            factory = ViewModelFactory.getInstance(this)
            favoriteViewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

            favoriteViewModel.getFavoriteArticleById(articles.id!!)?.observe(this) { favoriteArticle ->
                var isFavorite = favoriteArticle != null
                favoriteFab.setImageResource(if (isFavorite) R.drawable.ic_bookmarked_24 else R.drawable.ic_bookmark_border_24)

                favoriteFab.setOnClickListener {
                    if (isFavorite) {
                        favoriteViewModel.deleteFavoriteArticleById(articles.id)
                        Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        favoriteViewModel.insertFavoriteArticle(articles.toArticlesFavEntity()!!)
                        Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                    }
                    isFavorite = !isFavorite
                }
            }

            tvDetailTitle.text = articles.title
            tvDetailView.text = articles.totalView.toString()
            tvDetailWasteType.text = articles.wasteType
            tvDetailDescription.text = articles.desc
            tvDetailTools.text = HtmlCompat.fromHtml(formatTextWithDashes(articles.tools), HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvDetailSteps.text = HtmlCompat.fromHtml(formatTextWithDashes(articles.steps), HtmlCompat.FROM_HTML_MODE_LEGACY)

            Glide.with(this)
                .load(articles.pitcURL)
                .into(ivDetailPhoto)
        }
    }

    private fun formatTextWithDashes(text: String?): String {
        return text?.let {
            it.replace("\n", "<br/>-")
                .replace("-", "<br/>- ")
                .let { formattedText -> "- $formattedText" }
        } ?: ""
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

