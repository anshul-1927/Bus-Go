package dev.datvt.busfun.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import dev.datvt.busfun.R;
import dev.datvt.busfun.adapters.PaperAdapter;
import dev.datvt.busfun.utils.Variables;

public class PaperActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_new);
        PaperAdapter adapter = new PaperAdapter(this, R.id.tvPaper, Variables.PAPERS);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(PaperActivity.this, CategoryActivity.class);
        intent.putExtra(Variables.PAPER, position);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(PaperActivity.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
