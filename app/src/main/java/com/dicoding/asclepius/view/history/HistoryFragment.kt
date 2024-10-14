package com.dicoding.asclepius.view.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ViewModelFactory
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.databinding.FragmentHistoryBinding
import com.dicoding.asclepius.view.adapter.HistoryAdapter
import com.google.android.material.snackbar.Snackbar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        val historyAdapter = HistoryAdapter { historyEntity ->
            viewModel.deleteHistory(historyEntity)
            Snackbar.make(binding.root, "Histori berhasil dihapus", Snackbar.LENGTH_SHORT).show()
        }

        viewModel.getHistory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Results.Success -> {
                    binding.progressBar.visibility = View.GONE
                    historyAdapter.submitList(result.data)

                    if (result.data.isEmpty()) {
                        binding.tvEmptyMessage.visibility = View.VISIBLE // Show empty message
                    } else {
                        binding.tvEmptyMessage.visibility = View.GONE // Hide empty message
                    }
                }
                is Results.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, "Error: ${result.error}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
            Log.d("RecyclerView", "Adapter attached successfully")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}