package pl.xmcg.streambase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import pl.xmcg.streambase.watchfree.SzukajWatchfree;

public class Serwisy extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serwisy);
        final ListView lista = (ListView) findViewById(R.id.listViewSerwisy);
        String[] serwisy = new String[]{"WatchFree.to"};
        ArrayList<String> list = new ArrayList<>(Arrays.asList(serwisy));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_text_layout, list);
        lista.setAdapter(adapter);
        lista.setClickable(true);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String serwis = lista.getItemAtPosition(position).toString();
                if (serwis.equalsIgnoreCase("watchfree.to")) {
                    Intent intent = new Intent(getApplicationContext(), SzukajWatchfree.class);
                    startActivity(intent);
                }
            }
        });
    }
}
