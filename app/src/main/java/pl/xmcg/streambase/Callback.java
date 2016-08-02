package pl.xmcg.streambase;

import android.content.DialogInterface;

/**
 * Created by Lee on 02.06.2016.
 */
public interface Callback {
    void pobieranieZakonczone(String dane);

    void anullowac(DialogInterface dialog);
}
