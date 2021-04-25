package com.token.tokenator.ui.savedpassword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.token.tokenator.R
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Encryption
import com.token.tokenator.databinding.LayoutSavedTokenListItemBinding
import com.token.tokenator.model.Token

class SavedPasswordAdapter(private val clickListener: TokenListener) :
    ListAdapter<Token, SavedPasswordAdapter.ViewHolder>(SavedPasswordsDiffCallback) {

    private var savedTokenList = emptyList<Token>()

    class ViewHolder(private val itemBinding: LayoutSavedTokenListItemBinding) :
        RecyclerView.ViewHolder(
            itemBinding.root
        ) {
        fun bind(token: Token, clickListener: TokenListener) {
            itemBinding.token = token
            itemBinding.apply {
                tokenTitle.text = token.title
                tokenPlaceholder.text = "**********"
            }

            itemBinding.tokenVisibilityIcon.setOnClickListener {
                var login = token.login
                login = when (login) {
                    null -> "None"
                    else -> token.login?.let { Encryption.decrypt(it) }
                }
                when (itemBinding.tokenPlaceholder.text) {
                    "**********" -> {
                        itemBinding.tokenPlaceholder.text =
                            "Login: $login\nPassword: ${Encryption.decrypt(token.token)}"
                        itemBinding.tokenVisibilityIcon.setImageResource(R.drawable.ic_view)
                    }
                    else -> {
                        itemBinding.tokenPlaceholder.text = "**********"
                        itemBinding.tokenVisibilityIcon.setImageResource(R.drawable.ic_view_hidden)
                    }
                }
            }

            itemBinding.clickListener = clickListener

            itemBinding.copyIcon.setOnClickListener {
                Encryption.decrypt(token.token)?.let { decryptedToken ->
                    Clipuous.copyToClipboard(
                        decryptedToken,
                        itemBinding.root.context
                    )
                }
                Snackbar.make(
                    itemBinding.root.rootView,
                    itemBinding.root.resources.getText(R.string.toast_copied_to_clipboard),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return LayoutSavedTokenListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let{
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = savedTokenList[position]
        holder.bind(currentItem, clickListener)
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

class TokenListener(val clickListener: (token: Token) -> Unit) {
    fun onClick(token: Token) = clickListener(token)
}