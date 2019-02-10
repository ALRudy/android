package com.example.digi_move

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scroll.*

class ScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)
        crdHeaderView.post(Runnable {
            // parameters are (View headerView, int marginTop)
            concealerNSV.setHeaderView(crdHeaderView, 15)
        })
        linFooterView.post(Runnable {
            // parameters are (View footerView, int marginBottom)
            concealerNSV.setFooterView(linFooterView, 0)
        })
    }
}
