package com.jocoos.flipflop.sample.live

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jocoos.flipflop.FFFilterType
import com.jocoos.flipflop.sample.databinding.FilterListItemBinding
import com.jocoos.flipflop.sample.utils.OnRecyclerViewItemClickListener
import com.jocoos.flipflop.sample.utils.applyOptionsForRoundedCorners
import com.jocoos.flipflop.sample.utils.px

class FilterInfo(val filterType: FFFilterType, val name: String, val resId: Int)

class FilterAdapter(
    private var filterList: List<FilterInfo>,
    private val onItemClickListener: OnRecyclerViewItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun selectedPosition(position: Int) {
        this.selectedPosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = FilterListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterListViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FilterListViewHolder).bind(filterList[position], selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    private class FilterListViewHolder(
        private val binding: FilterListItemBinding,
        listener: OnRecyclerViewItemClickListener? = null
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imageFilter.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.invoke(itemView, position)
                }
            }
        }

        fun bind(filterInfo: FilterInfo, isSelected: Boolean) {
            Glide.with(binding.root)
                .load(filterInfo.resId)
                .apply(applyOptionsForRoundedCorners(10.px))
                .into(binding.imageFilter)

            binding.textFilter.text = filterInfo.name
            binding.filterBorder.isVisible = isSelected
        }
    }
}
