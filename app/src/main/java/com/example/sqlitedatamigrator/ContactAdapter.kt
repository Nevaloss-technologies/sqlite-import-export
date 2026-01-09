package com.example.sqlitedatamigrator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val context: Context,
    private var contactList: ArrayList<ContactModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Interface for Edit and Delete actions
    interface OnItemClickListener {
        fun onEditClick(contact: ContactModel)
        fun onDeleteClick(contact: ContactModel)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtName)
        val phone: TextView = itemView.findViewById(R.id.txtPhone)
        val email: TextView = itemView.findViewById(R.id.txtEmail)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.email.text = contact.email

        // Edit button click
        holder.btnEdit.setOnClickListener {
            listener.onEditClick(contact)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(contact)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    // List update karne ke liye function
    fun updateList(newList: ArrayList<ContactModel>) {
        contactList = newList
        notifyDataSetChanged()
    }
}