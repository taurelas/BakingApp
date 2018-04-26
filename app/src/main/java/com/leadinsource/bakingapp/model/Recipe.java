package com.leadinsource.bakingapp.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.leadinsource.bakingapp.db.DataContract;

/**
 * Represents a single Recipe received from JSON
 */
@Entity(tableName = DataContract.Recipe.TABLE_NAME)
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DataContract.Recipe.UID)
    public int uid;

    private String id;

    private String servings;
    @ColumnInfo(name = DataContract.Recipe.NAME)
    private String name;

    private String image;

    @Ignore
    public Ingredient[] ingredients;

    @Ignore
    public Step[] steps;

    public Ingredient[] getIngredients()
    {
        return ingredients;
    }

    public void setIngredients (Ingredient[] ingredients)
    {
        this.ingredients = ingredients;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getServings ()
    {
        return servings;
    }

    public void setServings (String servings)
    {
        this.servings = servings;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public Step[] getSteps ()
    {
        return steps;
    }

    public void setSteps (Step[] steps)
    {
        this.steps = steps;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ingredients = "+ingredients+", id = "+id+", servings = "+servings+", name = "+name+", image = "+image+", steps = "+steps+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(getIngredients(), flags);
        dest.writeString(this.id);
        dest.writeString(this.servings);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeTypedArray(getSteps(), flags);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.ingredients = in.createTypedArray(Ingredient.CREATOR);
        this.id = in.readString();
        this.servings = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.steps = in.createTypedArray(Step.CREATOR);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
