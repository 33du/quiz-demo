package com.oops.quiz_demo.activity;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oops.quiz_demo.R;
import com.oops.quiz_demo.database.AppDatabase;
import com.oops.quiz_demo.database.entity.Answer;
import com.oops.quiz_demo.database.entity.Question;
import com.oops.quiz_demo.fragment.QuestionFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PracticeActivity extends FragmentActivity {

    public static ArrayList<Question> list;

    public Button buttonNext;
    public Button buttonLast;
    public Button buttonRecord;

    public ViewPager pager;
    public String practiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Intent intent = getIntent();
        practiceType = intent.getStringExtra("practice_type");

        buttonLast = findViewById(R.id.button_last);
        buttonNext = findViewById(R.id.button_next);
        buttonRecord = findViewById(R.id.button_record);

        pager = findViewById(R.id.pager);

        loadQuestionList();
    }

    private ArrayList<Question> createQuestions() {
        ArrayList<Answer> answers = new ArrayList<>();
        answers.add(new Answer(1, "这是对的"));
        answers.add(new Answer(0, "这是错的"));

        Question q1 = new Question(1,0, "第一题", answers, 0, 0, 0);

        ArrayList<Answer> answers2 = new ArrayList<>();
        answers2.add(new Answer(1, "这是对的"));
        answers2.add(new Answer(1, "这是对的"));
        answers2.add(new Answer(0, "这是错的"));
        Question q2 = new Question(2, 0, "第二题", answers2, 0, 0, 0);

        ArrayList<Answer> answers3 = new ArrayList<>();
        answers3.add(new Answer(1, "这是对的"));
        answers3.add(new Answer(1, "这也是对的"));
        Question q3 = new Question(3, 0, "第三题", answers3, 0, 0, 0);

        ArrayList<Question> questionList = new ArrayList<>();
        questionList.add(q1);
        questionList.add(q2);
        questionList.add(q3);

        return questionList;
    }

    private void loadQuestionList() {
        ArrayList<Question> questionList = createQuestions();

        new LoadQuestionListAsyncTask(this, questionList).execute();
    }

    private static class LoadQuestionListAsyncTask extends AsyncTask<Void, Void, Integer> {

        //Prevent leak
        private WeakReference<Activity> weakActivity;
        private ArrayList<Question> questions;

        protected LoadQuestionListAsyncTask(Activity activity, ArrayList<Question> questions) {
            weakActivity = new WeakReference<>(activity);
            this.questions = questions;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            AppDatabase db = Room.databaseBuilder(weakActivity.get(),
                    AppDatabase.class, "database-name").build();
            db.questionDao().insertAll(questions);
            if (((PracticeActivity) weakActivity.get()).practiceType.equals("ordered")) {
                PracticeActivity.list = (ArrayList<Question>) db.questionDao().getAll();
            } else if (((PracticeActivity) weakActivity.get()).practiceType.equals("random")) {
                PracticeActivity.list = (ArrayList<Question>) db.questionDao().getAllRandom();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //bind view pager adapter
            final MyAdapter adapter = new MyAdapter(((PracticeActivity) weakActivity.get()).getSupportFragmentManager());
            final ViewPager pager = ((PracticeActivity) weakActivity.get()).pager;
            pager.setAdapter(adapter);

            Button buttonNext = ((PracticeActivity) weakActivity.get()).buttonNext;
            Button buttonLast = ((PracticeActivity) weakActivity.get()).buttonLast;
            Button buttonRecord = ((PracticeActivity) weakActivity.get()).buttonRecord;

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
            });

            buttonLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                }
            });

            buttonRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(weakActivity.get(), RecordActivity.class);
                    intent.putExtra("type", "practice");
                    weakActivity.get().startActivityForResult(intent, 1);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int position = data.getIntExtra("result", pager.getCurrentItem());
                pager.setCurrentItem(position);
            }
        }
    }


    public static class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position);
        }
    }
}
