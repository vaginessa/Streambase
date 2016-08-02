package pl.xmcg.streambase.watchfree;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import pl.xmcg.streambase.Callback;
import pl.xmcg.streambase.R;
import pl.xmcg.streambase.Util;

public class WatchfreeInfoFilm extends AppCompatActivity {

    HashMap<String, String> hosterzy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchfree_info_film);
        String nazwa = getIntent().getStringExtra("Nazwa");
        String url = getIntent().getStringExtra("URL");
        setTitle("Movie: " + nazwa);
        final ListView listView = (ListView) findViewById(R.id.hostListWatchfree);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nazwa = listView.getItemAtPosition(position).toString();
                String url = hosterzy.get(nazwa);
                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        Util.pobierzStrone(this, url, "Please wait", "Downloading data...", false, new Callback() {
            @Override
            public void pobieranieZakonczone(String dane) {
                Document dokument = Jsoup.parse(dane);
                String opis = dokument.select("div.movie_data div").first().text();
                int max_len = 500;
                if (opis.length() > max_len) {
                    opis = opis.substring(0, max_len - 3) + "...";
                }
                final String opis2 = opis;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.descWatchfree)).setText(opis2);
                        ((TextView) findViewById(R.id.helpWatchfree)).setText("\nSelect hoster:");
                    }
                });

                hosterzy = new HashMap<String, String>();
                Elements elements = dokument.select("div.list_links table");
                int i = 1;
                for (Element element : elements) {
                    Element td = element.getElementsByTag("tbody").first().getElementsByTag("tr").first().getElementsByAttributeValue("align", "left").first();
                    String hoster = "H" + i + ": " + td.text().split("-")[1].trim();
                    String link = "http://watchfree.to" + td.getElementsByTag("strong").first().getElementsByTag("a").first().attr("href");
                    i++;
                    hosterzy.put(hoster, link);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> lista = new ArrayList(hosterzy.keySet());
                        Collections.sort(lista, new Comparator<String>() {
                            @Override
                            public int compare(String lhs, String rhs) {
                                int i1 = Integer.valueOf(lhs.split(":")[0].substring(1));
                                int i2 = Integer.valueOf(rhs.split(":")[0].substring(1));
                                return i1 - i2;
                            }
                        });
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.listview_text_layout, lista);
                        listView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void anullowac(DialogInterface dialog) {
                //nic, bo nie da sie przerwac
            }
        });
    }


}
