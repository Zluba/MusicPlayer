package handsome.musicplayer;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView songView;
    private List<Song> songList;
    private MediaPlayer mediaPlayer;
    private Button mainPlay,mainNext;
    private SeekBar playTime;
    private TextView nowPlaying;
    private int playType;
    private static int ICON_PLAY=R.drawable.bfzn_004;
    private static int ICON_PAUSE=R.drawable.bfzn_003;
    private static final int UPDATE_PLAYTIME=1;
    private static final int PLAY_ORDER=0;
    private static final int PLAY_RANDOM=1;
    private static final int PLAY_SINGLE=2;
    private int previousSong,totalSong;
    private ImageView bgImage;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_PLAYTIME:
                    try {
                        playTime.setProgress(mediaPlayer.getCurrentPosition());
                    }catch(Exception e){

                    }
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*musicList=new ArrayList<>();
        musicView=(ListView)findViewById(R.id.musicView);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,musicList);
        musicView.setAdapter(adapter);*/
        //初始化
        songList=new ArrayList<>();
        mediaPlayer=new MediaPlayer();
        totalSong=0;
        previousSong=-1;
        playType=PLAY_ORDER;

        //控件初始化
        songView=(ListView)findViewById(R.id.songView);
        mainPlay=(Button)findViewById(R.id.mainPlay);
        mainNext=(Button)findViewById(R.id.mainNext);
        playTime=(SeekBar)findViewById(R.id.playTime);
        nowPlaying=(TextView)findViewById(R.id.nowPlaying);
        bgImage=(ImageView)findViewById(R.id.bgImage);

        nowPlaying.setBackgroundColor(Color.WHITE);
        bgImage.setImageResource(R.drawable.setsuna);
        bgImage.setImageAlpha(0x77);

        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            String title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//标题
            String artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int duration=cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            if(artist.compareTo("<unknown>")==0)
                artist="";
            songList.add(new Song(title,artist,url,duration));totalSong++;
        }
        if(cursor!=null)
            cursor.close();
        final SongAdapter adapter=new SongAdapter(this,R.layout.song,songList);

        songView.setAdapter(adapter);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playSong(i);
            }
        });

        mainPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mainPlay.setBackground(getResources().getDrawable(ICON_PLAY));
                    mediaPlayer.pause();
                }else {
                    mainPlay.setBackground(getResources().getDrawable(ICON_PAUSE));
                    mediaPlayer.start();
                }
            }
        });

        mainNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (playType){
                    case PLAY_ORDER:
                        if(previousSong==totalSong-1)
                            previousSong=-1;
                        playSong(previousSong+1);
                        break;
                    case PLAY_RANDOM:
                        playSong((int)(Math.random()*songList.size()));
                        break;
                    case PLAY_SINGLE:
                        playSong((int)(Math.random()*songList.size()));
                        break;
                }
            }
        });

        playTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(playTime.getProgress());
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (playType){
                    case PLAY_ORDER:
                        if(previousSong==totalSong-1)
                            previousSong=-1;
                        playSong(previousSong+1);
                        break;
                    case PLAY_RANDOM:
                        playSong((int)(Math.random()*songList.size()));
                        break;
                    case PLAY_SINGLE:
                        mediaPlayer.start();
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    void playSong(int i){
        Song song=songList.get(i);
        try {
            Log.e("url",song.getUrl());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mainPlay.setBackground(getResources().getDrawable(ICON_PAUSE));
            previousSong=i;
            playTime.setMax(song.getDuration());
            nowPlaying.setText("正在播放："+song.getTitle());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message=new Message();
                    message.what=UPDATE_PLAYTIME;
                    handler.sendMessage(message);
                    handler.postDelayed(this,1000);
                }
            }).start();
        }catch (Exception e){
            String errorInformation="Play failed!";
            Toast.makeText(MainActivity.this,errorInformation,Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.playOrder:
                playType=PLAY_ORDER;break;
            case R.id.playRandom:
                playType=PLAY_RANDOM;break;
            case R.id.playSingle:
                playType=PLAY_SINGLE;break;
            default:break;
        }
        return true;
    }
}
