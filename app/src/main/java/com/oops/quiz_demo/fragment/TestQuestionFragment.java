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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oops.quiz_demo.R;
import com.oops.quiz_demo.activity.TestActivity;
import com.oops.quiz_demo.database.entity.Answer;
import com.oops.quiz_demo.database.entity.Question;

import java.util.ArrayList;

public class TestQuestionFragment extends Fragment {

    Question question;
    ArrayList<Answer> answers;
    ViewPager pager;

    int position;

    TextView textQuestion;
    TextView textResult;

    LinearLayout layoutAnswers;

    ArrayList<CheckBox> boxAnswers;

    Button buttonConfirm;

    Button buttonNext;

    Boolean isAnswered;
    Boolean isCorrect;

    public TestQuestionFragment() {
        // Required empty public constructor
    }

    public static TestQuestionFragment newInstance(int position) {
        TestQuestionFragment fragment = new TestQuestionFragment();
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
        textResult = v.findViewById(R.id.text_result);

        buttonConfirm = v.findViewById(R.id.button_confirm);
        buttonConfirm.setVisibility(View.GONE);

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

        buttonNext = ((TestActivity) getActivity()).buttonNext;
        question = TestActivity.list.get(position);
        answers = question.answers;
        pager = ((TestActivity) getActivity()).pager;

        if (((TestActivity) getActivity()).showAnswers) {
            initShowAnswers();
        } else {
            init();
        }
    }

    private void compareWithResults() {
        isAnswered = false;
        isCorrect = true;

        for (int i = 0; i < answers.size(); i++) {
            if (boxAnswers.get(i).isChecked()) {
                isAnswered = true;

                if (answers.get(i).isCorrect == 0) {
                    isCorrect = false;
                }
            } else if (!boxAnswers.get(i).isChecked() && answers.get(i).isCorrect == 1) {
                isCorrect = false;
            }
        }

        if (isAnswered) {
            TestActivity.list.get(pager.getCurrentItem()).status = 1;
        } else {
            TestActivity.list.get(pager.getCurrentItem()).status = 0;
        }

        ((TestActivity) getActivity()).correctnessList.set(position, isCorrect);
    }

    private void initShowAnswers() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TestActivity.list != null) {
                    String count = (position + 1) + "/" + TestActivity.list.size() + ": ";
                    textQuestion.setText(count + question.content);

                    for (int i = 0; i < answers.size(); i++) {
                        final CheckBox box = boxAnswers.get(i);
                        box.setText(answers.get(i).content);
                        box.setVisibility(View.VISIBLE);

                        if (answers.get(i).isCorrect == 1) {
                            box.setButtonDrawable(R.drawable.ic_right);
                        }
                    }

                    buttonNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pager.setCurrentItem(pager.getCurrentItem() + 1);
                        }
                    });
                }
            }
        });
    }

    private void init() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TestActivity.list != null) {
                    String count = (position + 1) + "/" + TestActivity.list.size() + ": ";
                    textQuestion.setText(count + question.content);

                    for (int i = 0; i < answers.size(); i++) {
                        final CheckBox box = boxAnswers.get(i);
                        box.setText(answers.get(i).content);
                        box.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < answers.size(); i++) {
                        boxAnswers.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                compareWithResults();
                            }
                        });
                    }

                    buttonNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pager.setCurrentItem(pager.getCurrentItem() + 1);
                        }
                    });
                }
            }
        });
    }
}
