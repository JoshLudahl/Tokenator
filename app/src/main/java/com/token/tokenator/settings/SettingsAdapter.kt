package com.token.tokenator.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.token.tokenator.databinding.LayoutSettingGridItemBinding
import com.token.tokenator.model.SettingsItem

class SettingsAdapter(
    private val clickListener: SettingsListener
) : ListAdapter<SettingsItem, SettingsAdapter.ViewHolder>(SettingsItemDiffCallback) {

    private var settingsItemList = emptyList<SettingsItem>()

    class ViewHolder(private val itemBinding: LayoutSettingGridItemBinding) :
        RecyclerView.ViewHolder(
            itemBinding.root
        ) {
        fun bind(item: SettingsItem, clickListener: SettingsListener) {
            itemBinding.item = item
            itemBinding.clickListener = clickListener
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val item = LayoutSettingGridItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentItem = settingsItemList[position]
        holder.bind(currentItem, clickListener)
    }

    fun setItems(setSettingsItemList: List<SettingsItem>) {
        settingsItemList = setSettingsItemList
        submitList(settingsItemList)
    }

    override fun getItemCount() = settingsItemList.size
}

object SettingsItemDiffCallback : DiffUtil.ItemCallback<SettingsItem>() {
    override fun areItemsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
        return oldItem == newItem
    }
}

class SettingsListener(val clickListener: (setting: SettingsItem) -> Unit) {
    fun onClick(setting: SettingsItem) = clickListener(setting)
}
