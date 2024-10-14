package com.dicoding.asclepius.view.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.ViewModelFactory
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.databinding.FragmentNewsBinding
import com.dicoding.asclepius.view.adapter.NewsAdapter
import com.dicoding.asclepius.view.webview.WebBottomSheet
import com.google.android.material.snackbar.Snackbar

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsAdapter = NewsAdapter { url ->
            val bottomSheet = WebBottomSheet.newInstance(url)
            bottomSheet.show(parentFragmentManager, WebBottomSheet::class.java.simpleName)
        }

        viewModel.getNews.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Results.Success -> {
                    binding.progressBar.visibility = View.GONE
                    newsAdapter.submitList(result.data)
                }

                is Results.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}