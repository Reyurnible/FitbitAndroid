package io.github.reyurnible.fitbit

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import io.github.reyurnible.fitbit.auth.FitbitAuthManager
import io.github.reyurnible.fitbit.auth.FitbitAuthToken
import io.github.reyurnible.fitbit.auth.FitbitScope

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.login_button).setOnClickListener {
            Fitbit.instance.login(this, FitbitScope.values(), object : FitbitAuthManager.FitbitLoginCallback {
                override fun onLoginSuccessed(token: FitbitAuthToken) {
                    Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_SHORT).show()
                }

                override fun onLoginErrored(error: Throwable) {
                    Toast.makeText(this@MainActivity, "Login Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
