package com.example.gestordeperfumaria

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MonitorFragment() : Fragment() {

    companion object {
        var cOText: String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_monitor, container, false)

        val text = view.findViewById<TextView>(R.id.text)
        text.text = cOText

        return view
    }

}