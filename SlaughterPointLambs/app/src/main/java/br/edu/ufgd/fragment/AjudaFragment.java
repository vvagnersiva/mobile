package br.edu.ufgd.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import br.edu.ufgd.R;

public class AjudaFragment extends Fragment {
    private static final String EXTRA_TIPO = "mTipo";
    ProgressDialog prDialog;
    private WebView mWebView;

    public static AjudaFragment novaInstancia(String tipo) {
        Bundle params = new Bundle();
        params.putString(EXTRA_TIPO, tipo);
        AjudaFragment f = new AjudaFragment();
        f.setArguments(params);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ajuda_fragment, container, false);

        // Bloco 1.
        TextView htmlTextView1 = (TextView) view.findViewById(R.id.txAjudaBloco1);

        htmlTextView1.setText(Html.fromHtml(getString(R.string.ajuda1)));

        // Bloco 2.
        TextView htmlTextView2 = (TextView) view.findViewById(R.id.txAjudaBloco2);

        htmlTextView2.setText(Html.fromHtml(getString(R.string.ajuda2)));

        // Indica que este fragment deve preservar o seu estado.
        setRetainInstance(true);

        return view;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            prDialog = new ProgressDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            String carregando = getResources().getString(R.string.carregando);
            prDialog.setMessage(carregando);
            prDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (prDialog != null) {
                prDialog.dismiss();
            }
        }
    }
}