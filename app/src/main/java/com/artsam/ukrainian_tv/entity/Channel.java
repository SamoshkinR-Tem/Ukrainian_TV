package com.artsam.ukrainian_tv.entity;

public class Channel {
    private String id;
    private String name;
    private String description;
    private String tvURL;
    private String siteURL;
    private String logoURL;
    private String streamURL;
    private String youtubeURL;

    public Channel() {
    }

    public Channel(String id, String name, String description,
                   String tvURL, String siteURL, String logoURL,
                   String streamURL, String youtubeURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tvURL = tvURL;
        this.siteURL = siteURL;
        this.logoURL = logoURL;
        this.streamURL = streamURL;
        this.youtubeURL = youtubeURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTvURL() {
        return tvURL;
    }

    public void setTvURL(String tvURL) {
        this.tvURL = tvURL;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public void setLogoURL(String logoURL) {
        this.logoURL = logoURL;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getYoutubeURL() {
        return youtubeURL;
    }

    public void setYoutubeURL(String youtubeURL) {
        this.youtubeURL = youtubeURL;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tvURL='" + tvURL + '\'' +
                '}';
    }
}