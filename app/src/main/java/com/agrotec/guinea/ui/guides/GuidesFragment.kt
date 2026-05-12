package com.agrotec.guinea.ui.guides

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrotec.guinea.data.AppDatabase
import com.agrotec.guinea.data.TreatmentEntity
import com.agrotec.guinea.databinding.FragmentGuidesBinding
import com.agrotec.guinea.databinding.ItemTreatmentBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class GuidesFragment : Fragment() {
    private var _binding: FragmentGuidesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGuidesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TreatmentAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            val all = AppDatabase.getInstance(requireContext()).treatmentDao().getAll()
            adapter.submitList(all)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class TreatmentAdapter : RecyclerView.Adapter<TreatmentAdapter.VH>() {
    private var items = listOf<TreatmentEntity>()
    fun submitList(list: List<TreatmentEntity>) { items = list; notifyDataSetChanged() }

    inner class VH(val b: ItemTreatmentBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
        VH(ItemTreatmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val t = items[pos]
        h.b.tvCropName.text = "🌱 ${t.cropName}"
        h.b.tvPestName.text = t.pestName
        h.b.tvSymptoms.text = "Síntomas: ${t.symptoms}"
        h.b.tvTreatment.text = "Tratamiento: ${t.treatment}"
        h.b.tvPrevention.text = "Prevención: ${t.prevention}"
        val sev = when(t.severityLevel) { 1 -> "● Baja"; 2 -> "● Media"; else -> "● Alta" }
        h.b.tvSeverity.text = sev
        h.b.tvSeverity.setTextColor(h.b.root.context.getColor(when(t.severityLevel) {
            1 -> com.agrotec.guinea.R.color.accent_green
            2 -> com.agrotec.guinea.R.color.accent_yellow
            else -> com.agrotec.guinea.R.color.accent_red
        }))
    }
}
