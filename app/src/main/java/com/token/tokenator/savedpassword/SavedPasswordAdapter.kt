package com.token.tokenator.savedpassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.token.tokenator.databinding.LayoutSavedTokenListItemBinding
import com.token.tokenator.model.Token

class SavedPasswordAdapter :
    ListAdapter<Token, SavedPasswordAdapter.ViewHolder>(SavedPasswordsDiffCallback) {

    private var savedTokenList = emptyList<Token>()

    class ViewHolder(private val itemBinding: LayoutSavedTokenListItemBinding) :
        RecyclerView.ViewHolder(
            itemBinding.root
        ) {
        fun bind(token: Token) {
            itemBinding.apply {
                tokenTitle.text = token.title
                tokenPlaceholder.text = token.token
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val item = LayoutSavedTokenListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentITem = savedTokenList[position]
        holder.bind(currentITem)
    }

    fun setItems(tokenList: List<Token>) {
        savedTokenList = tokenList
        submitList(tokenList)
    }
}

object SavedPasswordsDiffCallback : DiffUtil.ItemCallback<Token>() {
    override fun areItemsTheSame(oldItem: Token, newItem: Token): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Token, newItem: Token): Boolean {
        return oldItem == newItem
    }
}
