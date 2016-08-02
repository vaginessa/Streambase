package pl.xmcg.streambase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Lee on 02.06.2016.
 */
public class Util {
    public static void pobierzStrone(Activity activity, final String urlStrony, String tytul, String tekst, boolean cancel, final Callback callback) {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setTitle(tytul);
        dialog.setMessage(tekst);
        dialog.setIndeterminate(true);
        if(cancel){
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.anullowac(dialog);
                }
            });
        }
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStrony);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    reader.close();
                    callback.pobieranieZakonczone(builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        }).start();
    }
}
