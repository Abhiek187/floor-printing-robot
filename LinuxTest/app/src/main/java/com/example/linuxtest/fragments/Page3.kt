package com.example.linuxtest.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linuxtest.databinding.FragmentPage3Binding

/**
 * A simple [Fragment] subclass.
 */
class Page3 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentPage3Binding.inflate(inflater, container, false).root
    }
}
