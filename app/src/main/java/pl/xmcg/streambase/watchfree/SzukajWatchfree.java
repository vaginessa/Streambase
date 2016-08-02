package pl.xmcg.streambase.watchfree;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import pl.xmcg.streambase.Callback;
import pl.xmcg.streambase.R;
import pl.xmcg.streambase.Util;

public class SzukajWatchfree extends AppCompatActivity {

    private HashMap<String, String> wyniki;
    private int strona;
    private String szukaj;
    private EditText poleSzukania;
    private ListView listaWyniki;
    private boolean annuluj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szukaj_watchfree);
        setTitle("WatchFree.to");
        poleSzukania = ((EditText) findViewById(R.id.txtSzukajWatchfree));
        listaWyniki = (ListView) findViewById(R.id.listViewWynikiWatchFree);
        poleSzukania.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    szukaj = poleSzukania.getText().toString();
                    wyniki = new HashMap<>();
                    strona = 1;
                    annuluj = false;
                    pobierzNastepna();
                    return true;
                }
                return false;
            }
        });
        listaWyniki.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nazwa = listaWyniki.getItemAtPosition(position).toString();
                String url = wyniki.get(nazwa);
                boolean jestSerial = url.contains("tv-show-online-free-putlocker");
                Intent intent = new Intent(getApplicationContext(), jestSerial ? WatchfreeInfoSerial.class : WatchfreeInfoFilm.class);
                intent.putExtra("Nazwa", nazwa);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        });
    }

    private boolean zapiszWyniki(String dane, HashMap<String, String> wyniki) {
        if (dane.contains("No results :(")) {
            return false;
        }
        Document dokument = Jsoup.parse(dane);
        Elements elementy = dokument.select("div.item a[href][title]");
        for (Element element : elementy) {
            wyniki.put(element.attr("title").substring(5).trim(), "http://www.watchfree.to/" + element.attr("href"));
        }
        return true;
    }

    private void pobierzNastepna() {
        String url = "http://www.watchfree.to/?sort=alphabet&keyword=" + URLEncoder.encode(szukaj).replace("%20", "+") + "&page=" + strona;
        Util.pobierzStrone(SzukajWatchfree.this, url, "Please wait", "Getting results... (Page " + strona + ")", true, new Callback() {
            @Override
            public void pobieranieZakonczone(String dane) {
                if (zapiszWyniki(dane, wyniki) && !annuluj) {
                    ++strona;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pobierzNastepna();
                        }
                    });
                } else {
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_text_layout, new ArrayList<String>(wyniki.keySet()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listaWyniki.setAdapter(adapter);
                        }
                    });
                }
            }

            @Override
            public void anullowac(DialogInterface dialog) {
                annuluj = true;
            }
        });
    }
}

