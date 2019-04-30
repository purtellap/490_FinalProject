package com.austin.finalproject.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insertFavorite(Favorite favorite);

    @Delete
    void removeFavorite(Favorite favorite);

    @Query("SELECT * FROM Favorite")
    List<Favorite> getAllFavorites();
}
