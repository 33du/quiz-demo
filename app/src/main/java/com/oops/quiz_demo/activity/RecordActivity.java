package com.oops.quiz_demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oops.quiz_demo.R;
import com.oops.quiz_demo.database.entity.Question;

import java.util.ArrayList;
import java.util.Queue;

public class RecordActivity extends AppCompatActivity {

    ArrayList<Question> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if (type.equals("practice")) {
            list = PracticeActivity.list;
        } else if (type.equals("test")) {
            list = TestActivity.list;
        }

        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new GridAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    public class GridAdapter extends BaseAdapter {
        private Context mContext;

        public GridAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setText(""+position);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            textView.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 150
            );
            textView.setLayoutParams(lp);

            Question question = list.get(position);
            if (question.status == 1) {
                textView.setBackgroundColor(getColor(R.color.colorGreen));
            } else if (question.status == 2) {
                textView.setBackgroundColor(getColor(R.color.colorRed));
            }
            return textView;
        }
    }
}
