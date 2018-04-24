package com.leadinsource.bakingapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.leadinsource.bakingapp.db.DataContract;

/**
 * Model for Ingredients received from JSON
 */
@Entity(tableName = DataContract.Ingredient.TABLE_NAME)
public class Ingredient implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    private String measure;

    private String ingredient;

    private String quantity;

    @ColumnInfo(name = DataContract.Ingredient.RECIPE_ID)
    public int recipeId;

    public String getMeasure ()
    {
        return measure;
    }

    public void setMeasure (String measure)
    {
        this.measure = measure;
    }

    public String getIngredient ()
    {
        return ingredient;
    }

    public void setIngredient (String ingredient)
    {
        this.ingredient = ingredient;
    }

    public String getQuantity ()
    {
        return quantity;
    }

    public void setQuantity (String quantity)
    {
        this.quantity = quantity;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [measure = "+measure+", ingredient = "+ingredient+", quantity = "+quantity+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
        dest.writeString(this.quantity);
    }

    public Ingredient() {
    }

    protected Ingredient(Parcel in) {
        this.measure = in.readString();
        this.ingredient = in.readString();
        this.quantity = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
