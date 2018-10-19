package com.oops.quiz_demo.database.entity;

public class Answer {
    public int isCorrect;
    public String content;

    public Answer(int isCorrect, String content) {
        this.content = content;
        this.isCorrect = isCorrect;
    }
}
