package com.oops.quiz_demo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oops.quiz_demo.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView learnText = findViewById(R.id.learn);
        final LinearLayout detailLayout = findViewById(R.id.detail_learn);
        learnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailLayout.getVisibility() == View.GONE) {
                    detailLayout.setVisibility(View.VISIBLE);
                } else {
                    detailLayout.setVisibility(View.GONE);
                }
            }
        });

        TextView test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });

        Button orderedButton = findViewById(R.id.button_ordered);
        Button randomButton = findViewById(R.id.button_random);

        orderedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
                intent.putExtra("practice_type", "ordered");
                startActivity(intent);
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
                intent.putExtra("practice_type", "random");
                startActivity(intent);
            }
        });
    }
}
