package com.example.fangdouyin;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface item_cardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(item_card itemCard);

    @Insert
    void insertAllItem(item_card... itemCard);



    @Update
    void updateItem(item_card itemCard);
    @Delete
    void delete(item_card itemCard);

    @Query("SELECT * FROM item_card")
    LiveData<List<item_card>> getAllItem();

    @Query("DELETE FROM item_card")
    void deleteAllItem();
}
