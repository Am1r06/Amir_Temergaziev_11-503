package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstScreen()
        }
    }
}

@Composable
fun FirstScreen() {
    val context = LocalContext.current

    val name = stringResource(R.string.student_name)
    val group = stringResource(R.string.student_group)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = name)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = group)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val resultString = "$name/$group"

            val intent = Intent(context, SecondActivity::class.java).apply {
                putExtra("EXTRA_RESULT_STRING", resultString)
            }
            context.startActivity(intent)
        }) {
            Text(text = stringResource(R.string.btn_open_second_screen))
        }
    }
}