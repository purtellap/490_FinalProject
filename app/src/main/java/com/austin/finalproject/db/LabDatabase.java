package com.austin.finalproject.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Favorite.class}, version = 1)
public abstract class LabDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
}
