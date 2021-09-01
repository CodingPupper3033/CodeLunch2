package com.example.codelunch2.notification;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codelunch2.R;

public class SeeFullMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_full_menu2);

        TextView textView = findViewById(R.id.fullNotificationTextView);
        textView.setText(getIntent().getStringExtra("text")); // TODO Get from past if no text in string?
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
}