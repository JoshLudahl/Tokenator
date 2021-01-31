package com.token.tokenator.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.token.tokenator.databinding.LayoutSettingGridItemBinding
import com.token.tokenator.model.SettingsItem

class SettingsAdapter (
    private val list: List<SettingsItem>,
    private val clickListener: SettingsListener
    ) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

        class ViewHolder(private val itemBinding: LayoutSettingGridItemBinding) :
                RecyclerView.ViewHolder(
                    itemBinding.root
                ) {
                    fun bind(item: SettingsItem, clickListener: SettingsListener) {
                        itemBinding.infoText.text = item.item
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
        val currentItem = list[position]
        holder.bind(currentItem, clickListener)
    }

    override fun getItemCount(): Int = list.size

}

class SettingsListener(val clickListener: (setting: SettingsItem) -> Unit) {
    fun onClick(setting: SettingsItem) = clickListener(setting)
}
