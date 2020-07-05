package com.ishaangupta.inventory

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ishaangupta.inventory.DTO.MainList
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Dashboard"
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)



        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val mainListName = view.findViewById<EditText>(R.id.ev_mainlist)

            dialog.setTitle("Add Main-List")
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (mainListName.text.isNotEmpty()) {
                    val mainList = MainList()
                    mainList.name = mainListName.text.toString()
                    dbHandler.addMainList(mainList)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }
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
    fun updateMainList(mainList : MainList)
    {
        val dialog = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val mainListName = view.findViewById<EditText>(R.id.ev_mainlist)

        mainListName.setText(mainList.name)
        dialog.setView(view)
        dialog.setTitle("Update Main-List")
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (mainListName.text.isNotEmpty()) {
                mainList.name = mainListName.text.toString()
                dbHandler.updateMainList(mainList)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }
    override fun onResume() {
        refreshList()
        super.onResume()
    }
    private fun refreshList(){
        rv_dashboard.adapter = DashboardAdapter(this, dbHandler.getMainLists())
    }

    class DashboardAdapter(val activity: DashboardActivity,val list: MutableList<MainList>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_dashboard, p0, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.mainListName.text = list[p1].name
            var i = list[p1].id.toInt()
            holder.mainListName.setTextColor(activity.getTextColor(i))

            holder.mainListName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_MAIN_LIST_ID, list[p1].id)
                intent.putExtra(INTENT_MAIN_LIST_NAME, list[p1].name)
                activity.startActivity(intent)
            }

            holder.menu.setOnClickListener{
                val popup = PopupMenu(activity,holder.menu)
                popup.inflate(R.menu.dashboard_child)

                popup.setOnMenuItemClickListener {

                    when(it.itemId){
                        R.id.menu_edit->{
                            activity.updateMainList(list[p1])
                        }
                        R.id.menu_delete->{
                            activity.dbHandler.deleteMainList(list[p1].id)
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
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val mainListName: TextView = v.findViewById(R.id.tv_mainlist_name)
            val menu : ImageView = v.findViewById((R.id.iv_menu))
        }
    }
}

