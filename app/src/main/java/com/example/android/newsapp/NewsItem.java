package com.example.android.newsapp;

public class NewsItem {

    String nTitle;
    String nSection;
    String nTag;
    StringBuilder nAuthor;
    String nDate;
    String nTime;
    String nUrl;

    public NewsItem( String Section, String Title, String Tag, StringBuilder Author, String Date, String Time, String Url) {
        this.nTitle = Title;
        this.nSection = Section;
        this.nTag = Tag;
        this.nAuthor = Author;
        this.nDate = Date;
        this.nTime = Time;
        this.nUrl = Url;
    }

    public String getTitle() {
        return nTitle;
    }

    public String getSection() {
        return nSection;
    }

    public String getTag() {
        return nTag;
    }

    public StringBuilder getAuthor() {
        return nAuthor;
    }

    public String getDate() {
        return nDate;
    }

    public String getTime() {
        return nTime;
    }

    public String getUrl() {
        return nUrl;
    }
}
