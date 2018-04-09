package helloworldnepal.com.ehrdashboards.utils;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import helloworldnepal.com.ehrdashboards.database.AppDatabase;
import helloworldnepal.com.ehrdashboards.entity.Link;

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static Link addLink(final AppDatabase db, Link link) {
        db.linkDao().insertAll(link);
        return link;
    }

    private static void populateWithTestData(AppDatabase db) {
        Link link = new Link();
        link.setDisplayValue("Bahmini");
        link.setUrl("https://google.com");
        addLink(db, link);

        List<Link> linkList = db.linkDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + linkList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}
