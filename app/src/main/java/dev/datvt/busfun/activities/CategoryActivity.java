package dev.datvt.busfun.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.CategoryAdapter;
import dev.datvt.busfun.models.RssItem;
import dev.datvt.busfun.utils.RssParser;
import dev.datvt.busfun.utils.TransparentProgressDialog;
import dev.datvt.busfun.utils.Variables;

public class CategoryActivity extends ListActivity {

    private int paper;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        Bundle bundle = getIntent().getExtras();
        paper = bundle.getInt(Variables.PAPER);
        setTitle(Variables.PAPERS[paper]);
        CategoryAdapter adapter = new CategoryAdapter(this,
                Variables.ICONS[paper], Variables.CATEGORIES[paper]);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int key = paper * 1000 + position;
        if (Variables.newsMap.get(key) == null) {
            dialog = ProgressDialog.show(this, "", "Loading "
                    + Variables.CATEGORIES[paper][position]);
            new CategoryTask().execute(position);
        } else {
            Intent intent = new Intent(CategoryActivity.this,
                    NewsListActivity.class);
            intent.putExtra(Variables.PAPER, paper);
            intent.putExtra(Variables.CATEGORY, position);
            startActivity(intent);
        }

    }

    class CategoryTask extends AsyncTask<Integer, Void, Void> {
        private int position;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Integer... params) {
            position = params[0];
            int key = paper * 1000 + position;
            RssParser parser = new RssParser();
            List<RssItem> items = parser
                    .getNewsList(Variables.LINKS[paper][position]);
            System.out.println("Rss size : " + items.size());
            Variables.newsMap.put(key, items);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            Intent intent = new Intent(CategoryActivity.this,
                    NewsListActivity.class);
            intent.putExtra(Variables.PAPER, paper);
            intent.putExtra(Variables.CATEGORY, position);
            startActivity(intent);
            super.onPostExecute(result);
        }

    }
}
