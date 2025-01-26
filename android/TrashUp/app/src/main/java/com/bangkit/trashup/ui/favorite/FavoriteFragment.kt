package com.bangkit.trashup.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bangkit.trashup.databinding.FragmentFavoriteBinding
import com.bangkit.trashup.ui.ArticlesAdapter
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.ui.detail.DetailArticlesActivity

class FavoriteFragment : Fragment(), ArticlesAdapter.OnItemClickCallback {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFav.layoutManager = layoutManager

        observeViewModel()

        return root
    }

    private fun observeViewModel() {
        viewModel.getAllFavoriteArticles()?.observe(viewLifecycleOwner) { favoriteEvents ->
            setEventData(favoriteEvents)
        }

    }

    private fun setEventData(e: List<ArticlesFavEntity>) {
        val event = e.map { favoriteEvent -> favoriteEvent.toDatasItem() }
        val adapter = ArticlesAdapter(this)
        adapter.submitList(event)
        binding.rvFav.adapter = adapter
    }

    override fun onItemClick(articles: DatasItem) {
        val intent = Intent(requireContext(), DetailArticlesActivity::class.java)
        intent.putExtra(DetailArticlesActivity.EXTRA_ARTICLES, articles)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}