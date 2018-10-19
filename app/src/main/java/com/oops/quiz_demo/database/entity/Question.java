package com.oops.quiz_demo.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity
public class Question {
    @PrimaryKey
    public int id;

    public int type; //0: multiple choice

    public String content;

    public ArrayList<Answer> answers;

    @ColumnInfo(name = "is_saved")
    public int isSaved;

    public int status; //0: not answered, 1: correct, 2: wrong

    @ColumnInfo(name = "is_wrong")
    public int isWrong;

    public Question(int id, int type, String content, ArrayList<Answer> answers,
                    int isSaved, int status, int isWrong) {
        this.isWrong = isWrong;
        this.isSaved = isSaved;
        this.type = type;
        this.content = content;
        this.answers = answers;
        this.status = status;
        this.id = id;
    }

    public static class Converters {
        @TypeConverter
        public static ArrayList<Answer> fromString(String str) {
            Type listType = new TypeToken<ArrayList<Answer>>() {}.getType();
            return new Gson().fromJson(str, listType);
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<Answer> list) {
            return new Gson().toJson(list);
        }
    }
}

