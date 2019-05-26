package com.example.musicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView mySongViewForSong;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySongViewForSong=(ListView)findViewById(R.id.mySongListView);
        //Get Permission
        runTimePermission();

    }
    public void runTimePermission() {
        // Using Dexter to get Permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // If Permission Granted
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // If Denied it will ask again after restart of app
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // Using token to get Permission
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    // Function to find songs
    public ArrayList<File> findSong(File file)
    {
        // Creating arraylist to store file name
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();

        // Finding Songs
        for(File singleFile: files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            }else {
                if(singleFile.getName().endsWith(".mp3") ||
                singleFile.getName().endsWith(".wav")){
                    if(!singleFile.getName().startsWith("."))
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    // Function to display songs in listView
    void  display() {
        // Calling function to get all Songs and store in arraylist mysongs
        final ArrayList<File> mySongs=findSong(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];

        // For loop to get all file name
        for(int i=0;i<mySongs.size();i++)
        {
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        // Putting all file name in listView
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        mySongViewForSong.setAdapter(myAdapter);

        // Using Itent to play song Activity and send Song name and arraulist containing songs
        mySongViewForSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname=mySongViewForSong.getItemAtPosition(position).toString();
                startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songname)
                        .putExtra("pos",position)
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
               System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
