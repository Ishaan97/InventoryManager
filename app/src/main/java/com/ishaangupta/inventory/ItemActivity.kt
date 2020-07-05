package com.ishaangupta.inventory

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ishaangupta.inventory.DTO.ListItem
import com.ishaangupta.inventory.DTO.MainList
import kotlinx.android.synthetic.main.activity_item.*
import kotlin.math.exp

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    var main_id : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        dbHandler = DBHandler(this)

        supportActionBar?.title = intent.getStringExtra(INTENT_MAIN_LIST_NAME)
        main_id = intent.getLongExtra(INTENT_MAIN_LIST_ID, -1)

        rv_item.layoutManager = LinearLayoutManager(this)

        fab_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val itemListName = view.findViewById<EditText>(R.id.ev_mainlist)
            dialog.setTitle("Add Item")
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (itemListName.text.isNotEmpty()) {
                    val item = ListItem()
                    item.itemName = itemListName.text.toString()
                    item.main_id = main_id
                    dbHandler.addListItem(item)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()

        }

    }
    fun updateItem(item : ListItem)
    {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_item_list, null)

        val itemListName = view.findViewById<EditText>(R.id.ev_item_list)

        itemListName.setText(item.itemName)

        val quantity = view.findViewById<EditText>(R.id.et_qty)
        quantity.setText(item.qty.toString())

        val expirymonth = view.findViewById<EditText>(R.id.et_expiry_month)
        expirymonth.setText(item.expiry_month)
        dialog.setTitle("Update Item")
        dialog.setView(view)

        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->

            if (itemListName.text.isNotEmpty()) {

                item.itemName = itemListName.text.toString()

                var q: Long = 0
                if (quantity.text.isNotEmpty()) {
                    val qty = quantity.text.toString()
                    q = qty.toLong()
                    if (q < 0) q = 0
                }
                item.qty = q

                if (expirymonth.text.isNotEmpty()) {
                    item.expiry_month = expirymonth.text.toString()
                }
                dbHandler.updateListItem(item)
                refreshList()
            }

        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }
    fun getTextColor(idx : Int) : Int
    {

        var textColor = arrayOf(
            ResourcesCompat.getColor(resources, R.color.textColor1, null),
            ResourcesCompat.getColor(resources, R.color.textColor2, null),
            ResourcesCompat.getColor(resources, R.color.textColor3, null),
            ResourcesCompat.getColor(resources, R.color.textColor4, null),
            ResourcesCompat.getColor(resources, R.color.textColor5, null))
        return textColor[idx%textColor.size]
    }
    fun viewItem(item : ListItem)
    {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_item_list, null)

        val itemListName = view.findViewById<TextView>(R.id.ev_item_list)

        itemListName.text = item.itemName

        val quantity = view.findViewById<TextView>(R.id.et_qty)
        quantity.text = item.qty.toString()

        val expirymonth = view.findViewById<TextView>(R.id.et_expiry_month)
        expirymonth.text = item.expiry_month

        dialog.setView(view)
        dialog.setTitle("View Item")
        dialog.setNegativeButton("Back") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }
    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList() {
        rv_item.adapter = ItemAdapter(this, dbHandler.getListItem(main_id))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    class ItemAdapter(val activity : ItemActivity, val list: MutableList<ListItem>) :

        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.itemListName.text = list[p1].itemName

            var i = list[p1].id.toInt()
            holder.itemListName.setTextColor(activity.getTextColor(i))

            holder.qty.text = list[p1].qty.toString()

            holder.buttonAdd.setOnClickListener {
                val qty  = holder.qty.text.toString()
                var q: Long = 0

                if (qty != "")
                {
                    q = qty.toLong()
                    q += 1

                }
                else
                {
                    q = 1
                }
                list[p1].qty =  q
                activity.dbHandler.updateListItem(list[p1])
                activity.refreshList()

            }

            holder.buttonMinus.setOnClickListener {
                val qty  = holder.qty.text.toString()
                var q: Long = 0

                if (qty != "")
                {
                    q = qty.toLong()
                    q = q-1
                    if (q < 0)
                    {
                        q = 0
                    }

                }
                else
                {
                    q = 0
                }
                list[p1].qty =  q
                activity.dbHandler.updateListItem(list[p1])
                activity.refreshList()
            }

            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)

                popup.setOnMenuItemClickListener {

                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateItem(list[p1])

                        }
                        R.id.menu_delete -> {
                            activity.dbHandler.deleteListItem(list[p1].id)
                            activity.refreshList()
                        }
//                        R.id.menu_mark_as_completed->{
//                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,true)
//                        }
//                        R.id.menu_reset->{
//                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id,false)
//                        }
                    }

                    true
                }
                popup.show()
            }
            holder.itemListName.setOnClickListener{
                activity.viewItem(list[p1])
            }


    }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemListName: TextView = v.findViewById(R.id.tv_itemlist_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu_item)
            val buttonAdd : Button = v.findViewById(R.id.button_add)
            val buttonMinus : Button = v.findViewById(R.id.button_minus)
            val qty : TextView = v.findViewById(R.id.tv_qty)


        }
    }
}
