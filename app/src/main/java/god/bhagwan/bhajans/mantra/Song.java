package god.bhagwan.bhajans.mantra;

public class Song {
    String songName;
    String lyrics;
    String resource;
    String location_source;
    String image;
    Song(String sN,String ly, String lc_rc, String rs, String img){
        songName= sN;
        lyrics=ly;
        resource=rs;
        location_source=lc_rc;
        image=img;
    }

}
