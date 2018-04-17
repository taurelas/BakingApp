package com.leadinsource.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single Recipe received from JSON
 */

public class Recipe implements Parcelable {
    private Ingredients[] ingredients;

    private String id;

    private String servings;

    private String name;

    private String image;

    private Step[] steps;

    public Ingredients[] getIngredients ()
    {
        return ingredients;
    }

    public void setIngredients (Ingredients[] ingredients)
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
        dest.writeTypedArray(this.ingredients, flags);
        dest.writeString(this.id);
        dest.writeString(this.servings);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeTypedArray(this.steps, flags);
    }

    public Recipe() {
    }

    protected Recipe(Parcel in) {
        this.ingredients = in.createTypedArray(Ingredients.CREATOR);
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
