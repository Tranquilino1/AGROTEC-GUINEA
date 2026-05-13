package com.agrotec.guinea.ui.market

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrotec.guinea.R
import com.agrotec.guinea.data.AppDatabase
import com.agrotec.guinea.data.MarketPriceEntity
import com.agrotec.guinea.databinding.FragmentMarketBinding
import com.agrotec.guinea.databinding.ItemMarketPriceBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketFragment : Fragment() {
    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MarketPriceAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            AppDatabase.getInstance(requireContext()).marketPriceDao().getAllFlow().collectLatest { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class MarketPriceAdapter : RecyclerView.Adapter<MarketPriceAdapter.VH>() {
    private var items = listOf<MarketPriceEntity>()
    fun submitList(list: List<MarketPriceEntity>) { items = list; notifyDataSetChanged() }

    inner class VH(val b: ItemMarketPriceBinding) : RecyclerView.ViewHolder(b.root)
    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
        VH(ItemMarketPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val p = items[pos]
        h.b.tvCropName.text = getCropEmoji(p.cropName) + " " + p.cropName
        h.b.tvPrice.text = "${p.priceXAF.toInt()} XAF"
        h.b.tvUnit.text = "por ${p.unit}"
        h.b.tvUpdated.text = "Actualizado: ${p.lastUpdated}"
        val (trendIcon, trendColor) = when (p.trend) {
            "up" -> Pair("▲ Subiendo", R.color.accent_green)
            "down" -> Pair("▼ Bajando", R.color.accent_red)
            else -> Pair("● Estable", R.color.accent_yellow)
        }
        h.b.tvTrend.text = trendIcon
        h.b.tvTrend.setTextColor(ContextCompat.getColor(h.b.root.context, trendColor))
    }

    private fun getCropEmoji(name: String) = when {
        name.contains("Maíz") -> "🌽"
        name.contains("Plátano") -> "🍌"
        name.contains("Yuca") -> "🥔"
        name.contains("Tomate") -> "🍅"
        name.contains("Cacao") -> "🍫"
        name.contains("Café") -> "☕"
        name.contains("Aguacate") -> "🥑"
        name.contains("Cacahuete") -> "🥜"
        name.contains("Caña") -> "🌿"
        else -> "🌾"
    }
}
