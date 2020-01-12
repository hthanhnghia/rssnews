package com.rssfetcher.models;

public class RssFeedEntry {
    private String published;
    private String updated;
    private String title;
    private String content;
    private RssFeedAuthor author;

    public String getUpdated() {
        return updated;
    }

    public RssFeedAuthor getAuthor() {
        return author;
    }

    public void setAuthor(RssFeedAuthor author) {
        this.author = author;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public void setAttributeValue(String attribute, String value) {
        switch(attribute) {
            case "published":
                this.setPublished(value);
                break;
            case "updated":
                this.setUpdated(value);
                break;
            case "title":
                this.setTitle(value);
                break;
            case "content":
                this.setContent(value);
                break;
            default:
                break;

        }
    }
}
