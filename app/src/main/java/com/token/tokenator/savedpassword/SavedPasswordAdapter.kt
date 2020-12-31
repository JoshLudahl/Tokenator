package com.token.tokenator.savedpassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.token.tokenator.databinding.LayoutSavedTokenListItemBinding
import com.token.tokenator.model.Token

class SavedPasswordAdapter: RecyclerView.Adapter<SavedPasswordAdapter.ViewHolder>() {

    private val savedPasswordsList = emptyList<Token>()

    class ViewHolder(private val item: LayoutSavedTokenListItemBinding): RecyclerView.ViewHolder(item.root) {

        fun bind(token: Token) {
            // TODO: 12/30/20 Bind the elements when the UI is complete
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedPasswordAdapter.ViewHolder {
        val item = LayoutSavedTokenListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: SavedPasswordAdapter.ViewHolder, position: Int) {
        val currentITem = savedPasswordsList[position]
        holder.bind(currentITem)
    }

    override fun getItemCount(): Int {
        return savedPasswordsList.size
    }

}
