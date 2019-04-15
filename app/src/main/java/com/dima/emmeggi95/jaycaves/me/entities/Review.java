package com.dima.emmeggi95.jaycaves.me.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Review implements Serializable {

    private String author;
    private String title;
    private String body;
    private double rating;
    private String date;
    private int likes;

    public Review(){
        // For db only
    }

    public Review(String author, String title, String body, double rating, String date, int likes) {
        this.author = author;
        this.title = title;
        this.body = body;
        this.rating = rating;
        this.date = date;
        this.likes = likes;
    }

    public static Comparator<Review> dateComparator = new Comparator<Review>() {

        public int compare(Review a1, Review a2) {

            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = formatter.parse(a1.getDate());
            } catch (ParseException e) {
                System.out.println("Error parsing date1");
            }
            try {
                date2 = formatter.parse(a2.getDate());
            } catch (ParseException e) {
                System.out.println("Error parsing date2");
            }
            return  date2.compareTo(date1);


        }};

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


}
