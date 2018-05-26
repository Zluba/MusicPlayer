package handsome.musicplayer;

/**
 * Created by Administrator on 2016/7/17.
 */
public class Song {
    private String title;
    private String artist;
    private String url;
    private int duration;
    public Song(String title,String artist,String url,int duration){
        this.title=title;
        this.artist=artist;
        this.url=url;
        this.duration=duration;
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getUrl(){
        return url;
    }
    public int getDuration(){
        return duration;
    }
}
