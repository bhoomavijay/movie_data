package com.example.vinod.movie_db;

/**
 * Created by vinod on 11/19/2016.
 */
public class RowItem {
    private String movie_name;
    private String pic;
    private String release_date;
    private String id;
    private String over_view;
    private String rating;
    public RowItem(String movie_name, String pic, String release_date, String id,String over_view,String rating)
    {
        this.movie_name=movie_name;
        this.pic=pic;
        this.release_date=release_date;
        this.id=id;
        this.over_view=over_view;
        this.rating=rating;
    }

    public String get_movie_name()
    {
        return  movie_name;
    }
    public String getPic()
    {
        return pic;
    }
    public String get_release_date()
    {
        return release_date;
    }
    public String get_id()
    {
        return  id;
    }
    public String get_overview()
    {
        return  over_view;
    }
    public String get_rating(){return rating;}
}
