package com.token.tokenator.ui.savedpassword

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.token.tokenator.R
import com.token.tokenator.databinding.LayoutSavedTokenListItemBinding
import com.token.tokenator.model.Token
import com.token.tokenator.utilities.Clipuous
import com.token.tokenator.utilities.Encryption

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
                listOf(loginLabel, loginValue).forEach {
                    it.visibility = View.GONE
                }
            }

            itemView.setOnClickListener {
                val bundle = bundleOf("token" to token.id.toString())
                findNavController(it).navigate(
                    R.id.action_savedTokenFragment_to_passwordDetailFragment,
                    bundle
                )
            }

            itemBinding.tokenVisibilityIcon.setOnClickListener {
                when (itemBinding.tokenPlaceholder.text) {
                    "**********" -> {
                        itemBinding.tokenPlaceholder.text = Encryption.decrypt(token.token)
                        itemBinding.tokenVisibilityIcon.setImageResource(R.drawable.ic_view)
                        itemBinding.loginValue.apply {
                            token.login?.let {
                                val login = Encryption.decrypt(it)
                                if (login?.trim()?.isNotEmpty() == true) {
                                    listOf(this, itemBinding.loginLabel).forEach { view ->
                                        view.visibility = View.VISIBLE
                                    }

                                    this.text = Encryption.decrypt(it)
                                }

                            }
                        }
                    }
                    else -> {
                        itemBinding.tokenPlaceholder.text = "**********"
                        itemBinding.tokenVisibilityIcon.setImageResource(R.drawable.ic_view_hidden)
                        listOf(itemBinding.loginLabel, itemBinding.loginValue).forEach {
                            it.visibility = View.GONE
                        }
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
        ).let {
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
