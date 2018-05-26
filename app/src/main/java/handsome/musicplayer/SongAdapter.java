package handsome.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/7/17.
 */
public class SongAdapter extends ArrayAdapter<Song> {
    private int resourceId;

    public SongAdapter(Context context,int textViewResourceId,List<Song> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Song song=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView title=(TextView)view.findViewById(R.id.title);
        TextView artist=(TextView)view.findViewById(R.id.artist);
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        return view;
    }
}
