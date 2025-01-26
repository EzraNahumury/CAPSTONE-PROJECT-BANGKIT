package com.bangkit.trashup.ui.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.trashup.ui.ArticlesAdapter
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bangkit.trashup.databinding.FragmentArticlesBinding
import com.bangkit.trashup.ui.detail.DetailArticlesActivity


class ArticlesFragment : Fragment(), ArticlesAdapter.OnItemClickCallback {

    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var articlesViewModel: ArticlesViewModel
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        articlesViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[ArticlesViewModel::class.java]

        articlesAdapter = ArticlesAdapter(this)

        binding.rvArticles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = articlesAdapter
        }

        articlesViewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data.let {
                        val shuffledArticles = it.shuffled()
                        articlesAdapter.submitList(shuffledArticles)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.error == "Login first") {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        articlesViewModel.updateViewResult.observe(viewLifecycleOwner) { result ->
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

        articlesViewModel.getStories()
    }

//    private fun getData() {
//        val adapter = ArticlesAdapter(this)
//        binding.rvArticles.adapter = adapter.withLoadStateFooter(
//            footer = LoadingStateAdapter{
//                adapter.retry()
//            }
//        )
//        articlesViewModel.stories.observe(viewLifecycleOwner) {
//            adapter.submitData(lifecycle, it)
//        }
//    }


    override fun onItemClick(story: DatasItem) {
        story.id?.let {
            story.wasteGroup?.let { wasteGroup ->
                articlesViewModel.updateArticleView(it, wasteGroup)
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
