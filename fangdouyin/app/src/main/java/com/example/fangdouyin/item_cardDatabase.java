package com.example.fangdouyin;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {item_card.class}, version = 2, exportSchema = false)
public abstract class item_cardDatabase extends RoomDatabase {
    private static volatile item_cardDatabase INSTANCE;
    public abstract item_cardDao getItemCardDao();
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    // 单例模式
    public static item_cardDatabase getDataBase(Context context){
        if (INSTANCE == null) {
            synchronized (item_cardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),// 必须是 Application 上下文，以满足全局的单例模式
                                    item_cardDatabase.class,
                                    "卡片数据库"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
