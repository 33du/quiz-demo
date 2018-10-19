package com.oops.quiz_demo.fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.oops.quiz_demo.R;
import com.oops.quiz_demo.activity.PracticeActivity;
import com.oops.quiz_demo.database.entity.Answer;
import com.oops.quiz_demo.database.entity.Question;

import java.util.ArrayList;

public class QuestionFragment extends Fragment {

    int position;

    TextView textQuestion;

    LinearLayout layoutAnswers;

    ArrayList<CheckBox> boxAnswers;

    Button buttonConfirm;

    TextView textResult;

    Button buttonNext;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(int position) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_question, container, false);

        textQuestion = v.findViewById(R.id.text_demo);
        layoutAnswers = v.findViewById(R.id.layout_answers);
        buttonConfirm = v.findViewById(R.id.button_confirm);
        textResult = v.findViewById(R.id.text_result);

        boxAnswers = new ArrayList<>();
        boxAnswers.add((CheckBox)v.findViewById(R.id.box1));
        boxAnswers.add((CheckBox)v.findViewById(R.id.box2));
        boxAnswers.add((CheckBox)v.findViewById(R.id.box3));
        boxAnswers.add((CheckBox)v.findViewById(R.id.box4));
        boxAnswers.add((CheckBox)v.findViewById(R.id.box5));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buttonNext = ((PracticeActivity) getActivity()).buttonNext;

        init();
    }

    private void init() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Question question;
                final ViewPager pager = ((PracticeActivity) getActivity()).pager;

                if (PracticeActivity.list != null) {
                    question = PracticeActivity.list.get(position);
                    String count = (position + 1) + "/" + PracticeActivity.list.size() + ": ";
                    textQuestion.setText(count + question.content);

                    final ArrayList<Answer> answers = question.answers;

                    for (int i = 0; i < answers.size(); i++) {
                        CheckBox box = boxAnswers.get(i);
                        box.setText(answers.get(i).content);
                        box.setVisibility(View.VISIBLE);
                    }

                    for (int i = answers.size(); i < 5; i++) {
                        CheckBox box = boxAnswers.get(i);
                        box.setVisibility(View.GONE);
                    }

                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean answeredCorrect = true;
                            for (int i = 0; i < answers.size(); i++) {
                                CheckBox box = boxAnswers.get(i);
                                if (answers.get(i).isCorrect == 1 && box.isChecked()) {
                                    box.setButtonDrawable(R.drawable.ic_right);
                                } else if (answers.get(i).isCorrect == 1 && !box.isChecked()) {
                                    box.setButtonDrawable(R.drawable.ic_lack);
                                    answeredCorrect = false;
                                } else if (answers.get(i).isCorrect == 0 && box.isChecked()) {
                                    box.setButtonDrawable(R.drawable.ic_wrong);
                                    answeredCorrect = false;
                                }
                            }

                            if (answeredCorrect) {
                                buttonConfirm.setVisibility(View.GONE);
                                textResult.setText("Correct!");
                                textResult.setVisibility(View.VISIBLE);
                                buttonNext.setEnabled(false);

                                question.status = 1;

                                if (pager.getCurrentItem() != answers.size() - 1) {
                                    new CountDownTimer(2000, 2000) {

                                        public void onTick(long millisUntilFinished) {}

                                        public void onFinish() {
                                            pager.setCurrentItem(pager.getCurrentItem() + 1);
                                            buttonNext.setEnabled(true);
                                        }
                                    }.start();
                                }
                            } else {
                                buttonConfirm.setVisibility(View.GONE);
                                textResult.setText("Wrong, here's some explanation...");
                                textResult.setVisibility(View.VISIBLE);

                                question.status = 2;
                            }
                        }
                    });

                }
            }
        });
    }
}
