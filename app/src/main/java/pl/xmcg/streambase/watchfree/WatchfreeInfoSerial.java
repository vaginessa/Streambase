package pl.xmcg.streambase.watchfree;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import pl.xmcg.streambase.Callback;
import pl.xmcg.streambase.R;
import pl.xmcg.streambase.Util;

public class WatchfreeInfoSerial extends AppCompatActivity {

    public static final int WYBIERZ_SEZON = 0, WYBIERZ_ODCINEK = 1, WYBIERZ_HOSTER = 2;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchfree_info_serial);
        final String url = getIntent().getStringExtra("URL");
        final String nazwa = getIntent().getStringExtra("Nazwa");
        final int tryb = getIntent().getIntExtra("Tryb", WYBIERZ_SEZON);
        final String sezon = getIntent().getStringExtra("Sezon");
        final String odcinek = getIntent().getStringExtra("Odcinek");
        setTitle("TV Show: " + nazwa + (sezon != null ? " [" + sezon + "]" : ""));
        listView = (ListView) findViewById(R.id.listViewSerialWatchfree);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nazwaSezon = listView.getItemAtPosition(position).toString();
                if (tryb == WYBIERZ_SEZON) {
                    Intent intent = new Intent(getApplicationContext(), WatchfreeInfoSerial.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("Nazwa", nazwa);
                    intent.putExtra("Tryb", WYBIERZ_ODCINEK);
                    intent.putExtra("Sezon", nazwaSezon);
                    startActivity(intent);
                }
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
                        ((TextView) findViewById(R.id.txtSerialWatchfree)).setText(opis2);
                        ((TextView) findViewById(R.id.txtHelpSerialWatchfree)).setText("\nSelect " + (tryb == WYBIERZ_SEZON ? "season" : tryb == WYBIERZ_ODCINEK ? "episode" : tryb == WYBIERZ_HOSTER ? "hoster" : "") + ":");
                    }
                });
                if (tryb == WYBIERZ_SEZON) {
                    Elements sezony = dokument.getElementsByClass("season-toggle");
                    ArrayList list = new ArrayList();
                    for (Element element : sezony) {
                        list.add(element.text());
                    }
                    final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.listview_text_layout, list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                        }
                    });
                }
                if (tryb == WYBIERZ_ODCINEK) {
                    Elements sezony = dokument.getElementsByClass("season-toggle");
                    Element szukany = null;
                    for (Element element : sezony) {
                        szukany = element;
                        if (element.text().equals(sezon)) break;
                    }
                    String id = szukany.attr("data-id");
                    Elements odcinki = dokument.getElementsByAttributeValue("data-id", id);
                    System.out.println(odcinki.size());
                }
            }

            @Override
            public void anullowac(DialogInterface dialog) {
                //nic, bo nie da sie przerwac
            }
        });
    }
}
