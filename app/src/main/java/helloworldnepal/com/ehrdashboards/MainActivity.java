package helloworldnepal.com.ehrdashboards;

import android.arch.persistence.room.Database;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import helloworldnepal.com.ehrdashboards.database.AppDatabase;
import helloworldnepal.com.ehrdashboards.entity.Link;
import helloworldnepal.com.ehrdashboards.utils.DatabaseInitializer;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ADD_LINK  = 201;

    ArrayList<Link> linkArrayList = new ArrayList<Link>();
    ArrayAdapter<Link> linkArrayAdapter;
    ListView linkListView;
    GetLinksFromDB getLinksFromDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linkListView = (ListView) findViewById(R.id.linkListView);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        linkListView.setEmptyView(emptyText);

        linkArrayAdapter = new ArrayAdapter<Link>(this, android.R.layout.simple_list_item_1, linkArrayList);
        linkListView.setAdapter(linkArrayAdapter);
        linkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webIntent = new Intent(MainActivity.this, WebActivity.class);
                webIntent.putExtra("display_value", linkArrayAdapter.getItem(position).getDisplayValue());
                webIntent.putExtra("url", linkArrayAdapter.getItem(position).getUrl());
                MainActivity.this.startActivity(webIntent);
            }
        });

        registerForContextMenu(linkListView);

        getLinksFromDB = new GetLinksFromDB(this);
        getLinksFromDB.execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(MainActivity.this, AddLinkActivity.class);
                MainActivity.this.startActivityForResult(addIntent, REQUEST_ADD_LINK);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_LINK) {
            if (resultCode == RESULT_OK) {
                if (getLinksFromDB.getStatus() == AsyncTask.Status.FINISHED) {
                    getLinksFromDB = new GetLinksFromDB(MainActivity.this);
                    getLinksFromDB.execute();
                }
            }
        }
    }



    private class GetLinksFromDB extends AsyncTask<Void, Integer, ArrayList<Link>> {
        private WeakReference<MainActivity> activityReference;
        private AppDatabase db;
        // only retain a weak reference to the activity
        GetLinksFromDB(MainActivity context) {
            activityReference = new WeakReference<>(context);
            db = AppDatabase.getAppDatabase(context);
        }

        @Override
        protected ArrayList<Link> doInBackground(Void... params) {


            ArrayList<Link> links = (ArrayList) db.linkDao().getAll();
            if(links.size() == 0) {
                DatabaseInitializer.populateSync(db);
                links = (ArrayList) db.linkDao().getAll();
            }

            return links;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(ArrayList<Link> linkArrayList) {
            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.linkArrayAdapter.clear();
            activity.linkArrayAdapter.addAll(linkArrayList);
            activity.linkArrayAdapter.notifyDataSetChanged();

        }
    }

    private class DeleteLink extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<MainActivity> activityReference;
        private AppDatabase db;
        private Link link;
        // only retain a weak reference to the activity
        DeleteLink(MainActivity context, Link link) {
            activityReference = new WeakReference<>(context);
            db = AppDatabase.getAppDatabase(context);
            this.link = link;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            db.linkDao().delete(link);
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.linkListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {

            case R.id.delete:
                Link link  = (Link) linkListView.getItemAtPosition(info.position);
                linkArrayAdapter.remove(link);
                new DeleteLink(MainActivity.this, link).execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
