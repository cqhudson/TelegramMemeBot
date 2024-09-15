public class MemeJson {

    private String postLink;
    private String subreddit;
    private String title;
    private String url;
    private Boolean nsfw;
    private Boolean spoiler;
    private String author;
    private Integer ups; // heckin updoots!
    private String[] preview;

    public String getPostLink() { return postLink; }
    public String getSubreddit() { return subreddit; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public Boolean getNsfw() { return nsfw; }
    public Boolean getSpoiler() { return spoiler; }
    public String getAuthor() { return author; }
    public Integer getUps() { return ups; }
    public String[] getPreview() { return preview; }

}
