package com.example.edwardlafontant.drinklist.models;

/**
 * Created by edwardlafontant on 10/23/16.
 */

public class DrinkModel {

    private String drink;
    private String tagline;
    private float rating;
    private String image;
    private String story;

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getDrink(){
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }



}
