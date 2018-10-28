package com.inficare.iremit.testapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aagito.imageradiobutton.RadioImageGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radioImageGroup.setOnCheckedChangeListener(object: RadioImageGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(radioGroup: View, radioButton: View?, isChecked: Boolean, checkedId: Int) {

                println(checkedId)

            }

        })
    }
}
