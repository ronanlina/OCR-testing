package com.example.connivingdog.textrecogtest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*

//import sun.security.krb5.internal.KDCOptions.with



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        buttonStart.setOnClickListener {
            var intent= Intent(this,RecognitionActivity::class.java)
            startActivity(intent)
        }
    }
}
