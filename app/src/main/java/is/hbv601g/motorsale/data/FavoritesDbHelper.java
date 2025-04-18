package is.hbv601g.motorsale.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;

/**
 * Helper class for managing local favorites database.
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "favorites.db";
    private static final int DB_VERSION = 1;

    private final Gson gson = new Gson();

    public FavoritesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "listingId TEXT, " +
                "listingJson TEXT, " +
                "UNIQUE(userId, listingId))";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(db);
    }

    /**
     * Adds a listing to user's favorites.
     */
    public void addFavorite(String userId, ListingDTO listing) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("listingId", String.valueOf(listing.getListingId()));
        values.put("listingJson", gson.toJson(listing));

        db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /**
     * Removes a listing from user's favorites.
     */
    public void removeFavorite(Long listingId, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "listingId = ? AND userId = ?", new String[]{String.valueOf(listingId), userId});
        db.close();
    }

    /**
     * Checks if a listing is already in user's favorites.
     */
    public boolean isFavorite(Long listingId, String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM favorites WHERE listingId = ? AND userId = ?",
                new String[]{String.valueOf(listingId), userId});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * Retrieves all listings favorited by a user.
     */
    public List<ListingDTO> getFavoritesForUser(String userId) {
        List<ListingDTO> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT listingJson FROM favorites WHERE userId = ?", new String[]{userId});

        while (cursor.moveToNext()) {
            String json = cursor.getString(cursor.getColumnIndexOrThrow("listingJson"));
            ListingDTO listing = gson.fromJson(json, ListingDTO.class);
            favorites.add(listing);
        }

        cursor.close();
        db.close();
        return favorites;
    }
}
