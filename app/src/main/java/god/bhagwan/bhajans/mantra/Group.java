package god.bhagwan.bhajans.mantra;

import java.util.ArrayList;

public class Group {
    Group(String grpName){
        groupName=grpName;
        songs=new ArrayList<Song>();
    }
    String groupName;
    ArrayList<Song> songs;
}
