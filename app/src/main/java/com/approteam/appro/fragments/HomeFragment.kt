package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.approteam.appro.data_models.Appro
import com.github.kittinunf.fuel.Fuel
import com.google.gson.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_list_item.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class HomeFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val approFragment = ApproFragment(c)
    private val createApproFragment = CreateApproFragment(c)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        val btn = view.findViewById<Button>(R.id.createButton)
        btn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)?.replace(R.id.container, createApproFragment)?.commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doAsync {
            Fuel.get("http://foxer153.asuscomm.com:3001/appro")
                .response { _, _, result ->
                    val (bytes,error) = result
                    if(bytes!=null){
                        Log.d("DBG", "Bytes received!")
                        Log.d("DBG", String(bytes))
                        val appros = Gson().fromJson(String(bytes),Array<Appro>::class.java).toList()
                        uiThread {
                            Log.d("DBG",appros.toString())
                            Log.d("DBG", "UI THREAD")
                            homeProgressBar.visibility = View.GONE
                            approClick(appros, String(bytes))
                        }
                    }
                }
        }

        approListRView.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
    }
    //Click handler for opening an appro
    fun approClick(appros: List<Appro>,approJson: String){
        approListRView.adapter = HomeViewAdapter(appros, c){
            val bundle = Bundle()
            bundle.putString("approName", it.name)
            bundle.putString("approDesc", it.description)
            bundle.putString("approPic", it.image)
            bundle.putString("approJson", approJson)
            approFragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()?.addSharedElement(cardImage,it.name!!)?.addToBackStack(null)
                ?.replace(R.id.container, approFragment)?.commit()
        }
    }


}