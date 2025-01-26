package com.bangkit.trashup.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.trashup.databinding.FragmentHomeBinding
import com.bangkit.trashup.ui.ArticlesAdapter
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bangkit.trashup.ui.detail.DetailArticlesActivity

class HomeFragment : Fragment(), ArticlesAdapter.OnItemClickCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storyAdapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]

        storyAdapter = ArticlesAdapter(this)

        binding.rvPopularTutor.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }

        homeViewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data.let { articles ->
                        val sortedArticles = articles.sortedByDescending { it.totalView }

                        val topArticles = sortedArticles.take(5)

                        storyAdapter.submitList(topArticles)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.updateViewResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show() // Pesan sukses
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        homeViewModel.getStories()
        configureMapWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureMapWebView() {
        val webView: WebView = binding.webViewMaps
        val webSettings: WebSettings = webView.settings

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                callback.invoke(origin, true, false)
            }
        }

        val mapHtml = """
        <html>
        <body>
        <iframe 
            src="https://www.google.com/maps/d/u/0/embed?mid=1VR5wH1YBy3y6mSYNWwVCSD0HllmMie9n"
            width="100%"
            height="100%"
            style="border:0;"
            allowfullscreen=""
            loading="lazy">
        </iframe>
        </body>
        </html>
    """.trimIndent()

        webView.loadData(mapHtml, "text/html", "utf-8")
    }

    override fun onItemClick(story: DatasItem) {
        story.id?.let {
            story.wasteGroup?.let { wasteGroup ->
                homeViewModel.updateArticleView(it, wasteGroup)
            }
        }

        val intent = Intent(requireContext(), DetailArticlesActivity::class.java)
        intent.putExtra(DetailArticlesActivity.EXTRA_ARTICLES, story)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
