package com.oops.quiz_demo.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.oops.quiz_demo.database.entity.Question;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Question> questions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question question);

    @Query("SELECT * FROM question")
    List<Question> getAll();

    @Query("SELECT * FROM question WHERE id IN (:ids)")
    List<Question> getByIds(List<Integer> ids);

    @Query("SELECT * FROM question ORDER BY random()")
    List<Question> getAllRandom();
}
