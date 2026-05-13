package com.agrotec.guinea.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrotec.guinea.data.AppDatabase
import com.agrotec.guinea.data.DiagnosisEntity
import com.agrotec.guinea.databinding.FragmentHistoryBinding
import com.agrotec.guinea.databinding.ItemDiagnosisBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DiagnosisAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).diagnosisDao().getAllFlow().collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.tvTotal.text = "${list.size} diagnósticos realizados"
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class DiagnosisAdapter : RecyclerView.Adapter<DiagnosisAdapter.VH>() {
    private var items = listOf<DiagnosisEntity>()
    fun submitList(list: List<DiagnosisEntity>) { items = list; notifyDataSetChanged() }

    inner class VH(val b: ItemDiagnosisBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
        VH(ItemDiagnosisBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val d = items[pos]
        h.b.tvPestName.text = d.pestName
        h.b.tvCropName.text = "🌱 ${d.cropName}"
        h.b.tvConfidence.text = "Confianza: ${d.confidence}"
        h.b.tvTreatment.text = d.treatment
        val sdf = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale("es"))
        h.b.tvDate.text = sdf.format(Date(d.timestamp))
    }
}
