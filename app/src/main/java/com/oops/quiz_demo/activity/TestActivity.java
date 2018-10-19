package com.oops.quiz_demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oops.quiz_demo.R;
import com.oops.quiz_demo.database.AppDatabase;
import com.oops.quiz_demo.database.entity.Answer;
import com.oops.quiz_demo.database.entity.Question;
import com.oops.quiz_demo.fragment.QuestionFragment;
import com.oops.quiz_demo.fragment.TestQuestionFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    public static ArrayList<Question> list;
    public ArrayList<ArrayList> answerList;
    public ArrayList<Boolean> correctnessList;

    public Button buttonNext;
    public Button buttonLast;
    public Button buttonRecord;
    public Button buttonSubmit;

    public ViewPager pager;
    public MyAdapter adapter;

    public Boolean showAnswers;

    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        showAnswers = getIntent().getBooleanExtra("show_answers", false);

        adapter = new MyAdapter(getSupportFragmentManager());

        buttonLast = findViewById(R.id.button_last);
        buttonNext = findViewById(R.id.button_next);
        buttonRecord = findViewById(R.id.button_record);
        buttonSubmit = findViewById(R.id.button_submit);
        timer = findViewById(R.id.timer);

        if (!showAnswers) {
            new CountDownTimer(120000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long minute = millisUntilFinished / 60000;
                    long second = (millisUntilFinished % 60000) / 1000;
                    timer.setText(String.format("%02d", minute) + ":" + second);
                }

                public void onFinish() {
                    submit();
                }
            }.start();
        }

        if (showAnswers) {
            timer.setVisibility(View.GONE);
            buttonRecord.setVisibility(View.GONE);
            buttonSubmit.setVisibility(View.GONE);
        }

        answerList = new ArrayList<>();
        correctnessList = new ArrayList<>();

        pager = findViewById(R.id.pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int currentPage;

            public final int getCurrentPage() {
                return currentPage;
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (showAnswers) {
            loadWrongQuestions();
        } else {
            loadQuestionList();
        }
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

        new TestActivity.LoadQuestionListAsyncTask(this, questionList).execute();
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
            TestActivity.list = (ArrayList<Question>) db.questionDao().getAllRandom();

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            final MyAdapter adapter = ((TestActivity) weakActivity.get()).adapter;
            final ViewPager pager = ((TestActivity) weakActivity.get()).pager;
            pager.setAdapter(adapter);

            Button buttonSubmit = ((TestActivity) weakActivity.get()).buttonSubmit;
            Button buttonLast = ((TestActivity) weakActivity.get()).buttonLast;
            Button buttonRecord = ((TestActivity) weakActivity.get()).buttonRecord;

            ArrayList<ArrayList> answerList = ((TestActivity) weakActivity.get()).answerList;
            ArrayList<Boolean> correctnessList = ((TestActivity) weakActivity.get()).correctnessList;
            for (int i = 0; i < TestActivity.list.size(); i++) {
                ArrayList<Integer> intList = new ArrayList<>();
                answerList.add(intList);
                correctnessList.add(Boolean.FALSE);
            }

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
                    intent.putExtra("type", "test");
                    weakActivity.get().startActivityForResult(intent, 1);
                }
            });

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TestActivity) weakActivity.get()).submit();
                }
            });
        }
    }




    private void loadWrongQuestions() {
        ArrayList<Integer> wrongList = getIntent().getIntegerArrayListExtra("wrong_list");

        new TestActivity.LoadWrongQuestionsAsyncTask(this, wrongList).execute();
    }

    private static class LoadWrongQuestionsAsyncTask extends AsyncTask<Void, Void, Integer> {

        //Prevent leak
        private WeakReference<Activity> weakActivity;
        private ArrayList<Integer> questions;

        protected LoadWrongQuestionsAsyncTask(Activity activity, ArrayList<Integer> questions) {
            weakActivity = new WeakReference<>(activity);
            this.questions = questions;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            AppDatabase db = Room.databaseBuilder(weakActivity.get(),
                    AppDatabase.class, "database-name").build();

            TestActivity.list = (ArrayList<Question>) db.questionDao().getByIds(questions);

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            final MyAdapter adapter = ((TestActivity) weakActivity.get()).adapter;
            final ViewPager pager = ((TestActivity) weakActivity.get()).pager;
            pager.setAdapter(adapter);

            Button buttonSubmit = ((TestActivity) weakActivity.get()).buttonSubmit;
            Button buttonLast = ((TestActivity) weakActivity.get()).buttonLast;
            Button buttonRecord = ((TestActivity) weakActivity.get()).buttonRecord;

            ArrayList<ArrayList> answerList = ((TestActivity) weakActivity.get()).answerList;
            ArrayList<Boolean> correctnessList = ((TestActivity) weakActivity.get()).correctnessList;
            for (int i = 0; i < TestActivity.list.size(); i++) {
                ArrayList<Integer> intList = new ArrayList<>();
                answerList.add(intList);
                correctnessList.add(Boolean.FALSE);
            }

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
                    intent.putExtra("type", "test");
                    weakActivity.get().startActivityForResult(intent, 1);
                }
            });

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TestActivity) weakActivity.get()).submit();
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

    private void submit() {
        int numCorrect = 0;
        int numWrong = 0;
        final ArrayList<Integer> wrongList = new ArrayList<>();

        for (int i = 0; i < correctnessList.size(); i++) {
            if (correctnessList.get(i)) {
                numCorrect++;
            } else {
                numWrong++;
                wrongList.add(list.get(i).id);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("测试结束")
                .setMessage("你答对了"+numCorrect+"题，答错了"+numWrong+"题。")
                .setPositiveButton("显示错题", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TestActivity.this, TestActivity.class);
                        intent.putExtra("show_answers", true);
                        intent.putIntegerArrayListExtra("wrong_list", wrongList);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }


    public class MyAdapter extends FragmentPagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return TestQuestionFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
