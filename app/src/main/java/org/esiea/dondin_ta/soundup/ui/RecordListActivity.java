package org.esiea.dondin_ta.soundup.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.esiea.dondin_ta.soundup.R;
import org.esiea.dondin_ta.soundup.dao.Db;
import org.esiea.dondin_ta.soundup.dao.RecordDao;
import org.esiea.dondin_ta.soundup.model.BaseRecord;
import org.esiea.dondin_ta.soundup.model.RecordAwr;
import org.esiea.dondin_ta.soundup.util.Global;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private Db db;
    private RecordDao recordDao;

    private MediaPlayer mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        recordDao = new RecordDao(this);

        adapter = new SimpleCursorAdapter(this,R.layout.record_list_item,null,new String[]{"_id","name","createTime","length"},new int[]{R.id._idItem,R.id.nameItem,R.id.createTimeItem,R.id.lengthItem});
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(RecordListActivity.this).setTitle(R.string.deletetitle).setMessage(R.string.deleteconf)
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Cursor c = adapter.getCursor();
                                c.moveToPosition(position);
                                int itemId = c.getInt(c.getColumnIndex("_id"));
                                if (recordDao.delRecord(itemId)) {
                                    Toast.makeText(RecordListActivity.this,R.string.deleting, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(RecordListActivity.this,R.string.faildeleting, Toast.LENGTH_LONG).show();
                                }
                                refreshListView();
                            }
                        }).setNegativeButton("cancel", null).show();
                // Suppression valide, retourne true
                return true;
            }
        });

        //clic
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Lecture
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String fileName = cursor.getString(cursor.getColumnIndex("name"));
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(Global.PATH + fileName);
                    mPlayer.prepare();
                    //mPlayer.start();
                } catch (Exception e) {
                    Toast.makeText(RecordListActivity.this, "Fichier manquant", Toast.LENGTH_LONG).show();

                    new AlertDialog.Builder(RecordListActivity.this).setTitle("you need an updated List").setMessage("Do you want to update?")
                            .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            updateRecordList();
                                            refreshListView();
                                        }
                                    }
                            ).setNegativeButton("cancel", null).show();
                }
            }
        });

        refreshListView();

        //Joue le son
        Button playMusicBtn = (Button) findViewById(R.id.play_music);
        playMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer != null){
                    mPlayer.start();
                }
            }
        });

    }

    public void refreshListView(){
        adapter.changeCursor(recordDao.getAllRecord());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Playlist
     */
    public void updateRecordList(){
        // Analyser le dossier de tous les fichiers enregistrés (suffixe numérisée), et les données sont écrites dans la base de données.
       // Obtenir l'emplacement spécifié les paramètres du fichier afficher à la playlist

        recordDao.clearRecord();
        File rootPath = new File(Global.PATH);
        if (rootPath.listFiles(new RecordFilter()).length > 0){
            for (File file : rootPath.listFiles(new RecordFilter())){
                BaseRecord record = new RecordAwr();
                record.setName(file.getName());
                record.setRecordFile(file);
                record.setCreateTime(new Date(file.lastModified()));
                record.setLength(file.length());
                recordDao.addRecord(record);
            }
        }
    }

    class RecordFilter implements FilenameFilter {
        public boolean accept(File dir, String name){
            return (name.endsWith(".amr") || name.endsWith(".wav"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        recordDao.close();
    }
}
