package god.bhagwan.bhajans.mantra;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String packageName = "god.bhagwan.bhajans.mantra";

    MediaPlayer mediaPlayer;
    DrawerLayout mdrawerLayout;
    String offlineSongsLocation = Environment.getExternalStorageDirectory() + File.separator + ".ngsongs";
    String webDomainURL = "http://testdomainb-tk.stackstaging.com";
    String webDomainURLSongsDownloads = webDomainURL + "/uploads/";
    String webDomainURLApi = webDomainURL + "/android_api.php?package_name=" + packageName;
    private ActionBarDrawerToggle mdrawerToggle;
    NavigationView nv;
    PresongsDTO initerDBSongs;
    ListView mDrawerList;

    ListView songsList;
    ArrayList<Group> groups;
    String groupNames[];
    String songNames[];
    ImageView godImage;
    Dialog setWallpaperDialog;

    RelativeLayout progressContent;
    TextView downloadingValue;

    ImageView playPauseButton;
    private ProgressBar mProgressBar;
    int currentGroupPlaying = 0;
    int currentSongPlaying = 0;
    TextView songName;
    Context appContext;
    InterstitialAd mInterstitialAd;
    WallpaperManager wallpaperManager;


    //===================================activity life cycle methods start==============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = this;

        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawerToggle = new ActionBarDrawerToggle(this, mdrawerLayout, R.string.openDrawer, R.string.closeDrawer);
        mdrawerLayout.addDrawerListener(mdrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        songsList = (ListView) findViewById(R.id.songsList);
        godImage = (ImageView) findViewById(R.id.godImage);
        nv = (NavigationView) findViewById(R.id.nav_view);
        playPauseButton = (ImageView) findViewById(R.id.playpause);
        groups = new ArrayList<Group>();
        mdrawerLayout.openDrawer(nv);
        initerDBSongs = new PresongsDTO();
        songName = (TextView) findViewById(R.id.songName);

        progressContent = (RelativeLayout) findViewById(R.id.progressContent);
        downloadingValue = (TextView) findViewById(R.id.downloadingValue);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        wallpaperManager = WallpaperManager.getInstance(this);


        setWallpaperDialog = new Dialog(this);
        setWallpaperDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setWallpaperDialog.setContentView(getLayoutInflater().inflate(R.layout.set_as_wallpaper,null));



        progressContent.setVisibility(View.INVISIBLE);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        // AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        // "827C83CB6F95569F1AA8385F8BCC4BD1") // An example device ID
        // .build();
        if (mAdView != null)
            mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(appContext);
        // mInterstitialAd
        // .setAdUnitId("ca-app-pub-7605970282562833/9211881909");
        mInterstitialAd
                .setAdUnitId("ca-app-pub-7605970282562833/9211881909");

        AdRequest interstitialadRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(interstitialadRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            checkinitialSongsLoad();
            playGroupSongs(0, true);
        }
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mdrawerLayout.closeDrawer(nv);

                playGroupSongs(position, false);

                if (getRandomBoolean() && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });
        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                playOnTap(position);
            }
        });

        godImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setWallpaperDialog.show();
                return true;
            }
        });
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        playNxt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setWallpaper(View v){

        ArrayList<Song> mysongs = groups.get(currentGroupPlaying).songs;
        Song song = mysongs.get(currentSongPlaying);
        try {
            if (song.location_source.equals("res")) {
                int drawableID = getResources().getIdentifier(song.image, "drawable", getPackageName());
                wallpaperManager.setResource(drawableID);
            } else {
                Uri contentURI=getImageContentUri(appContext,offlineSongsLocation + File.separator + song.image);
                try {
                    Intent intent = wallpaperManager.getCropAndSetWallpaperIntent(contentURI);
                    startActivityForResult(intent, 19);     //some random number
                } catch (IllegalArgumentException e) {
                    // Seems to be an Oreo bug - fall back to using the bitmap instead
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(appContext.getContentResolver(), contentURI);
                    wallpaperManager.setBitmap(bitmap);
                }
            }
        }
        catch(Exception e){
            Log.d("bhanuchandsongs", "failed to set wallpaper : ");
            e.printStackTrace();
        }
        setWallpaperDialog.hide();
    }

    public static Uri getImageContentUri(Context context, String absPath) {

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[] { MediaStore.Images.Media._ID }
                , MediaStore.Images.Media.DATA + "=? "
                , new String[] { absPath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }
    //===================================activity life cycle methods end==============================


    //=============method which checks if songs have to be added to db for very first time===========
    void checkinitialSongsLoad() {
        SharedPreferences shp = getSharedPreferences("songsapp", MODE_PRIVATE);
        SharedPreferences.Editor shpe = shp.edit();
        String initDone = shp.getString("init", null);
        if (initDone != null) {
            displayLoadedSongs();
        } else {
            if (initerDBSongs.insertToDB(this)) {
                shpe.putString("init", "songs playing from db");
                shpe.commit();
                displayLoadedSongs();
            } else {
                //failed please load app again
            }
        }
    }


    //=========================Main method which fetches songs from db and renders to screen==============
    void displayLoadedSongs() {
        Cursor mycursor = initerDBSongs.displayRaw(this);
        while (mycursor.moveToNext()) {
            String groupName = mycursor.getString(2);
            int grpIndex = groupsIndexOf(groupName);
            if (grpIndex < 0) {
                Song song = new Song(mycursor.getString(0), mycursor.getString(1), mycursor.getString(3), mycursor.getString(4), mycursor.getString(5));
                Group grp = new Group(groupName);
                grp.songs.add(song);
                groups.add(grp);
            } else {
                Song song = new Song(mycursor.getString(0), mycursor.getString(1), mycursor.getString(3), mycursor.getString(4), mycursor.getString(5));
                Group xx = groups.get(grpIndex);
                xx.songs.add(song);
            }
        }
        convertGroupsToGroupNames();
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, groupNames));
    }

    void mediaPlayerCallBack() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    playNxt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //================================Main Method which plays songs================================================
    void playIterateSongs(ArrayList<Song> mysongs, boolean pause) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            Song song = mysongs.get(currentSongPlaying);
            songName.setText(song.songName);
            if (song.location_source.equals("res")) {
                int resID = getResources().getIdentifier(song.resource, "raw", getPackageName());
                int drawableID = getResources().getIdentifier(song.image, "drawable", getPackageName());
                godImage.setImageResource(drawableID);
                mediaPlayer = MediaPlayer.create(this, resID);
            } else {
                Uri envpath = Uri.parse(offlineSongsLocation + File.separator + song.resource);

                Uri imageName = Uri.parse(offlineSongsLocation + File.separator + song.image);
                godImage.setImageURI(imageName);

                mediaPlayer = MediaPlayer.create(this, envpath);
            }
            if (!pause) {
                mediaPlayer.start();
                mediaPlayerCallBack();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            } else {
                mediaPlayer.start();
                mediaPlayerCallBack();
                playPauseButton.setImageResource(R.drawable.ic_play);
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            mediaPlayer = null;
        }
    }


    //========================= UI control options for song start================================================
    void playNxt() {
        Group mygroup = groups.get(currentGroupPlaying);
        ArrayList<Song> mysongs = mygroup.songs;
        currentSongPlaying++;
        currentSongPlaying = currentSongPlaying % (mysongs.size());
        playIterateSongs(mysongs, false);
    }


    public void playNext(View v) {
        playNxt();
    }

    public void playPause(View v) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);
        }
    }

    public void playOnTap(int index) {
        Group mygroup = groups.get(currentGroupPlaying);
        ArrayList<Song> mysongs = mygroup.songs;
        currentSongPlaying = index;
        playIterateSongs(mysongs, false);
    }

    public void playPrevious(View v) {
        Group mygroup = groups.get(currentGroupPlaying);
        ArrayList<Song> mysongs = mygroup.songs;
        currentSongPlaying--;
        if (currentSongPlaying < 0) currentSongPlaying = mysongs.size() - 1;
        playIterateSongs(mysongs, false);
    }

    void playGroupSongs(int id, boolean pause) {        //initial iterator for playing
        if (id == groups.size()) {
            downloadSongsFromNet();
            return;
        }
        Group mygroup = groups.get(id);
        currentGroupPlaying = id;
        currentSongPlaying = 0;
        setTitle(groupNames[id]);
        ArrayList<Song> mysongs = mygroup.songs;
        playIterateSongs(mysongs, pause);
        displaySongNames(mysongs);
    }

    void displaySongNames(ArrayList<Song> mysongs) {

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < mysongs.size(); i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", mysongs.get(i).songName);
            if (mysongs.get(i).location_source.equals("res")) {
                int resID = getResources().getIdentifier(mysongs.get(i).resource, "raw", getPackageName());
                int drawableID = getResources().getIdentifier(mysongs.get(i).image, "drawable", getPackageName());
                hm.put("listview_image", "" + drawableID);
            } else {
                //code yet to come
                hm.put("listview_image", "" + R.drawable.cow);
            }
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title"};
        int[] to = {R.id.songs_list_item_image, R.id.songs_list_item_text};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.songs_list_item, from, to);
        songsList.setAdapter(simpleAdapter);
    }
    //==============================UI control options for song end=================================================


    //========================Utility functions for code modularization start===============================

    int groupsIndexOf(String groupName) {
        for (int i = 0; i < groups.size(); i++)
            if (groupName.equals(groups.get(i).groupName)) return i;
        return -1;
    }

    void convertGroupsToGroupNames() {
        groupNames = new String[groups.size() + 1];
        for (int i = 0; i < groups.size(); i++) groupNames[i] = groups.get(i).groupName;
        groupNames[groups.size()] = "Download More Bhajans";
    }

    //========================Utility functions for code modularization end===============================


    //==========================Creates thread and does downloading in background=============================
    void downloadSongsFromNet() {
        if (isReadWriteStoragePermissionGranted()) {

            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                Snackbar snackbar = Snackbar.make(mdrawerLayout, "No Internet Connection", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(mdrawerLayout, "Download Started", Snackbar.LENGTH_LONG);
                snackbar.show();
                new DownloadFilesTask().execute("started");
            }
        } else {
            //already handled at permission screen
            //Snackbar snackbar = Snackbar.make(mdrawerLayout, "need read write access for downloading", Snackbar.LENGTH_LONG);
            //snackbar.show();
        }
    }


    //=============================ASYNC TASK START===============================================================

    class DownloadFilesTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            String url_string = webDomainURLApi;
            String jsonSongsDownloaded = "";
            try {
                java.net.URL url = new URL(url_string);
                URLConnection urlConnection = url.openConnection();
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                InputStreamReader inreader = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inreader);
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonSongsDownloaded = stringBuilder.toString();
                int totalAdditionalSongsDownloaded = 0;
                JSONArray jr = new JSONArray(jsonSongsDownloaded);
                DatabaseHelper dbh = new DatabaseHelper(appContext);
                JSONObject songs;
                jsonSongsDownloaded = "0";
                for (int i = 0; i < jr.length(); i++) {
                    songs = (JSONObject) jr.getJSONObject(i);
                    String name = songs.getString("name");
                    String lyrics = songs.getString("lyrics");
                    String groupName = songs.getString("groupName");
                    groupName = groupName.replace("\r", "");     //duplicates were being formed because of this
                    String path = songs.getString("path");
                    if (!checkSongIfAlreadyExist(groupName, path)) {
                        String imageId = songs.getString("imageName");
                        if (downloadSongOffline(path) && downloadSongOffline(imageId)) {
                            dbh.insertData(name, lyrics, groupName, "local", path, imageId);
                            totalAdditionalSongsDownloaded++;
                        }
                    }
                    publishProgress(((i + 1) * 100) / jr.length());
                }
                jsonSongsDownloaded = "" + totalAdditionalSongsDownloaded;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonSongsDownloaded;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("bhanuchandsongs", values[0].toString());
            downloadingValue.setText(values[0].toString() + "%");
            mProgressBar.setProgress(values[0]);

            super.onProgressUpdate(values);
        }

        boolean checkSongIfAlreadyExist(String groupName, String path) {
            Group grpCheck = null;
            for (int i = 0; i < groups.size(); i++) {
                String tmp = groups.get(i).groupName;
                if (tmp.equals(groupName)) {
                    grpCheck = groups.get(i);
                    break;
                }
            }
            if (grpCheck != null) {
                for (int i = 0; i < grpCheck.songs.size(); i++) {
                    if (grpCheck.songs.get(i).resource.equals(path)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }

        boolean downloadSongOffline(String urlString) {
            try {
                File folder = new File(offlineSongsLocation);
                if (folder.exists() || (!folder.exists() && folder.mkdirs())) {
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + ".ngsongs" + File.separator + urlString);
                    urlString = webDomainURLSongsDownloads + urlString;
                    java.net.URL url = new URL(urlString);
                    URLConnection urlConnection = url.openConnection();
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(toByteArray(urlConnection.getInputStream()));
                    fos.close();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public byte[] toByteArray(InputStream in) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            return os.toByteArray();
        }

        @Override
        protected void onPreExecute() {
            progressContent.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            progressContent.setVisibility(View.INVISIBLE);
            if (result.equals("0")) {
                Snackbar snackbar = Snackbar.make(mdrawerLayout, "Already Up to Date!", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(mdrawerLayout, result + " Songs Downloaded reloading to updates", Snackbar.LENGTH_LONG);
                snackbar.show();

                //code to restart app in 1 seconds, change timings if u want bro
                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                System.exit(0);

            }
        }
    }
    //=============================ASYNC TASK END===============================================================


    //===========================Needed for drawyer layout start=================================================
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mdrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mdrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mdrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    //==========================Needed for drawer layout end=====================================================


    public boolean getRandomBoolean() {
        return new Random().nextBoolean();
    }

    //=================================PERMISSION ZONE START=====================================================
    public boolean isReadWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("bhanu", "Permission is granted1");
                return true;
            } else {
                Log.v("bhanu", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("bhanu", "Permission is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.v("bhanu", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            downloadSongsFromNet();
        } else {
            Snackbar snackbar = Snackbar.make(mdrawerLayout, "need read write access for downloading", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    //=====================================Permission ZONE END==============================================


}
