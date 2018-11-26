package scu.csci187.fall2018.mealtracker.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SQLiteDBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shoppingListDB";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        onCreate(db);
    }

    public void initShoppingList() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text)";
        db.execSQL(sql);
    }
    public void addEntry(SQLiteMeal meal) {
        ContentValues cv = new ContentValues();
        cv.put("meal", meal.getMealName());
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.insert("shoppingList",null,  cv);


        String dropCurrentTable = "DROP TABLE IF EXISTS " + meal.getMealName();
        db.execSQL(dropCurrentTable);
        String sql = "CREATE TABLE IF NOT EXISTS " + meal.getMealName() + "(ingredient text PRIMARY KEY, checkmark integer)";
        db.execSQL(sql);

        ArrayList<SQLiteIngredient> ingredients = meal.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            //String sql1 = "insert into " + meal.getMealName() + "(ingredient, checkmark) values (" + ingredients.get(i).getIngredient()+ ", 0)";
            //Log.d("DBMANAGER", ingredients.get(i));
            ContentValues cv1 = new ContentValues();
            Log.d("DBMANAGER", ingredients.get(i).getIngredient());
            cv1.put("ingredient", ingredients.get(i).getIngredient());
            cv1.put("checkmark", 0);

            db.insert(meal.getMealName(),null, cv1);

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void writeToDB(ArrayList<SQLiteMeal> meals){

        for (SQLiteMeal slm: meals) {

        }
        SQLiteDatabase db = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS shoppingList(_id integer primary key autoincrement, meal text)";
        db.execSQL(sql);
        for(int i = 0; i < meals.size(); i++) {
            String currentMeal = meals.get(i).getMealName();
            ArrayList<SQLiteIngredient> currentIngredients;
            currentIngredients = meals.get(i).getIngredients();
            ContentValues cv = new ContentValues();
            cv.put("meal", currentMeal);
            db.insert("shoppingList", null, cv);
            sql = "CREATE TABLE IF NOT EXISTS " + currentMeal + "(ingredient text PRIMARY KEY, checkmark integer)";
            db.execSQL(sql);
            for(int j = 0; j < currentIngredients.size(); j++) {
                Log.d("sqldbman", currentIngredients.get(j).getIngredient());
                ContentValues cv2 = new ContentValues();
                cv2.put("ingredient", currentIngredients.get(j).getIngredient());
                cv2.put("checkmark", currentIngredients.get(j).getisChecked());
                db.insert(currentMeal, null, cv2);
            }
        }
    }

    public void clearDatabase(String mealName) {
        SQLiteDatabase db = getWritableDatabase();
        String sql;
        sql = "drop table if exists " + mealName;
        db.execSQL(sql);
        sql = "delete from shoppingList where meal="+"\""+mealName+"\"";
        db.execSQL(sql);
    }

    public ArrayList<SQLiteMeal> getMeals(){
        Cursor cursor;
        try {
            cursor = this.getReadableDatabase().rawQuery("SELECT meal FROM shoppingList", null);
        } catch (Exception e) {
            cursor = null;
            e.getStackTrace();
        }
        ArrayList<SQLiteMeal> meals = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            ArrayList<SQLiteIngredient> ingredients;
            while (cursor.moveToNext()) {
                ingredients = new ArrayList<>();
                String currentMeal = cursor.getString(cursor.getColumnIndexOrThrow("meal"));
                Cursor Icursor = getReadableDatabase().rawQuery("SELECT * FROM " + currentMeal, null);
                if(Icursor != null) {
                    Icursor.moveToFirst();
                    while(Icursor.moveToNext()) {
                        String ingredientName = Icursor.getString(Icursor.getColumnIndexOrThrow("ingredient"));
                        boolean isChecked = (Icursor.getInt(Icursor.getColumnIndexOrThrow("checkmark")) != 0);
                        SQLiteIngredient ingredient = new SQLiteIngredient(ingredientName, isChecked);
                        ingredients.add(ingredient);
                    }
                }
                SQLiteMeal meal = new SQLiteMeal(currentMeal, ingredients);
                meals.add(meal);
                Icursor.close();
            }
        }
        if (cursor != null){
            cursor.close();
        }
        return meals;
    }
}