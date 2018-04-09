package helloworldnepal.com.ehrdashboards;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import helloworldnepal.com.ehrdashboards.database.AppDatabase;
import helloworldnepal.com.ehrdashboards.entity.Link;
import helloworldnepal.com.ehrdashboards.utils.DatabaseInitializer;

public class AddLinkActivity extends AppCompatActivity {
    TextInputLayout displayValueLayout, urlLayout;
    Button createButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_link);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Link");

        displayValueLayout = findViewById(R.id.displayValueTextInputLayout);
        urlLayout = findViewById(R.id.urlTextInputLayout);
        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLinkFromInput();
            }
        });

    }

    private void saveLinkFromInput() {
        boolean errors = false;
        String display_value = displayValueLayout.getEditText().getText().toString();
        if(display_value.length() == 0) {
            errors = true;
            displayValueLayout.getEditText().setError("Display Value is Required");
        }

        String url = urlLayout.getEditText().getText().toString();
        if(! Patterns.WEB_URL.matcher(url).matches()) {
            errors = true;
            urlLayout.getEditText().setError("A valid URL is required");
        }

        if (errors) return;

        Link link = new Link();
        link.setUrl(url);
        link.setDisplayValue(display_value);
        createButton.setEnabled(false);
        new InsertLink(this, link).execute();
    }

    private class InsertLink extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddLinkActivity> activityReference;
        private AppDatabase db;
        private Link link;
        // only retain a weak reference to the activity
        InsertLink(AddLinkActivity context, Link link) {
            activityReference = new WeakReference<>(context);
            db = AppDatabase.getAppDatabase(context);
            this.link = link;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            db.linkDao().insertAll(link);
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // get a reference to the activity if it is still there
            AddLinkActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.setResult(RESULT_OK);
            activity.finish();

        }
    }
}
