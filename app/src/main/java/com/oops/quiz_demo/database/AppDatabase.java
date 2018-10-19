package com.oops.quiz_demo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.oops.quiz_demo.database.dao.QuestionDao;
import com.oops.quiz_demo.database.entity.Question;

@Database(entities = {Question.class}, version = 1)
@TypeConverters({Question.Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();
}
