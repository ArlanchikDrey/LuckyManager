package com.arlanov.taskplanner

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.arlanov.taskplanner.RecyclerViewAdapters.RecyclerAdapterHead
import com.arlanov.taskplanner.Utils.Dialog.CustomBottomSheet
import com.arlanov.taskplanner.Utils.Listeners.ListenerPositionToday
import com.arlanov.taskplanner.Utils.Listeners.ListenerUpdateAdapters
import kotlinx.android.synthetic.main.activity_main.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


class MainActivity : AppCompatActivity() {

    private var items = LinkedHashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener {
           val dialog = CustomBottomSheet()
           dialog.setListenerUpdateAdapters(object : ListenerUpdateAdapters {
               override fun updateListener() {
                   createCalendar()
               }
           })
           dialog.show(supportFragmentManager,"TAG")
        }

        createCalendar()
    }

    private fun createCalendar(){
        Handler().post {
            val formatter = SimpleDateFormat("yyyy-MM-dd")

            try {
                val startDate = formatter.parse("2019-05-20")
                val endDate = formatter.parse("2020-05-18")

                val start = Calendar.getInstance()
                start.time = startDate
                val end = Calendar.getInstance()
                end.time = endDate

                var date = start.time
                while (start.before(end)) {
                    val f = String.format("%1\$ta %1\$te %1\$tb", date)
                    items.add(f)
                    start.add(Calendar.DATE, 1)
                    date = start.time

                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }


            initRecyclerView(items)
        }

    }

    private fun initRecyclerView(items: LinkedHashSet<String>) {
        val manager = object : LinearLayoutManager(this,HORIZONTAL,false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        val recyclerView: RecyclerView = recyclerViewHead
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = manager

        val arraysTime = items.toCollection(ArrayList<String>())

        recyclerView.adapter = RecyclerAdapterHead(
                this,
                arraysTime,
                getDateToday(),
                ListenerPositionToday {
                    i -> recyclerView.scrollToPosition(i)
                })
    }

    private fun getDateToday():String{
        val c = Calendar.getInstance()
        c.timeInMillis = Calendar.getInstance().timeInMillis

        val s = String.format("%1\$ta %1\$te %1\$tb", c)
        return s
    }

    fun update() {
        createCalendar()
    }



}
