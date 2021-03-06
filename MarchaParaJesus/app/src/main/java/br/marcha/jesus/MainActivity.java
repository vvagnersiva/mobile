package br.marcha.jesus;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.marcha.jesus.fragments.AgendaFragment;
import br.marcha.jesus.fragments.ConfigFragment;
import br.marcha.jesus.fragments.ContatoFragment;
import br.marcha.jesus.fragments.PaypalFragment;
import br.marcha.jesus.fragments.SobreFragment;
import br.marcha.jesus.fragments.VideosFragment;
import br.marcha.jesus.util.Constantes;
import br.marcha.jesus.mapas.LocalizacaoActivity;
import br.marcha.jesus.youtube.VideoEntry;
import br.marcha.jesus.youtube.VideoListActivity;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    ArrayList<VideoEntry> VIDEO_LIST;

    PayPalConfiguration payPalConfig = new PayPalConfiguration()
            .environment(Constantes.PAYPAL_ENV)
            .clientId(Constantes.PAYPAL_CLIENT_ID)
            .languageOrLocale("pt_BR");

    GoogleApiClient mGoogleApiClient;
    LatLng mOrigem;

    Handler mHandler;
    int mTentativas;
    Marker mMarkerLocalAtual;

    static final int COD_PAGTO = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Aqui eh a magica (A toolbar sera a action bar).
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fragment default.
        Fragment agendaFragment = new AgendaFragment();
        replaceFragment(agendaFragment);

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        // Thread p/ pesquisa dos links no youtube.
        pesquisarLinks();

        //Thread p/ obter a ultima localizacao.
        obterLocalizacao();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d("NGVL", "onConnected::BEGIN");
        verificarStatusGPS();

        Log.d("NGVL", "onConnected::END");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        List<VideoEntry> list = new ArrayList<VideoEntry>();

        Fragment fragment;

        switch (id) {
            case R.id.action_gps: {
                Intent it = new Intent(this, LocalizacaoActivity.class);

                // Iniciamos nossa activity
                startActivity(it);
                break;
            }

            case R.id.action_agenda: {
                fragment = new AgendaFragment();
                replaceFragment(fragment);

              /*  DialogFragment newFragment = FragmentDialogAlarmActivity.MyAlertDialogFragment
                        .newInstance(R.string.alert_dialog_two_buttons_title);
                newFragment.show(getFragmentManager(), "dialog");
*/
                break;
            }
            case R.id.action_videos: {
                fragment = new VideosFragment();
                replaceFragment(fragment);
                break;
            }
            case R.id.action_youtube: {
                Intent it = new Intent(this, VideoListActivity.class);
                it.putParcelableArrayListExtra("dados", (ArrayList) VIDEO_LIST);
                // Iniciamos nossa activity
                startActivity(it);
                break;
            }
            case R.id.action_contato: {
                fragment = new ContatoFragment();
                replaceFragment(fragment);
                break;
            }
            case R.id.action_paypal: {
                fragment = new PaypalFragment();
                replaceFragment(fragment);
                break;
            }
            case R.id.action_config: {
                fragment = new ConfigFragment();
                replaceFragment(fragment);
                break;
            }
            case R.id.action_sobre: {
                fragment = new SobreFragment();
                replaceFragment(fragment);
                break;
            }
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickEnviarEmail(View v) {
        String subject = "Escreva a sua mensagem";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_CC, new String[]{"vvagner.silva@gmail.com", "cabeh@globo.com"});
        //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, "");

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    public void onClickDoar(View v) {
        executarPagtoPayPal();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.layout_fragments, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private PayPalPayment montarPagtoFinal(Double valor) {
        // Valor da doacao.
        BigDecimal valDoacao = new BigDecimal(valor);

        PayPalPayment payPalPayment =
                new PayPalPayment(valDoacao, Constantes.PAYPAL_CURRENCY, "Doação", Constantes.PAYPAL_INTENT);

        PayPalPaymentDetails detalhes = new PayPalPaymentDetails(BigDecimal.ZERO, valDoacao, BigDecimal.ZERO);

        payPalPayment.custom("Doação - Marcha para Jesus");

        return payPalPayment;
    }

    private void executarPagtoPayPal() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        if (spinner.getSelectedItem().toString().length() == 0) {
            Toast.makeText(this, "Entre com o valor da doação!", Toast.LENGTH_SHORT).show();
            return;
        }

        double val = Double.parseDouble(spinner.getSelectedItem().toString());

        PayPalPayment pagto = montarPagtoFinal(val);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, pagto);

        startActivityForResult(intent, COD_PAGTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COD_PAGTO) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Pagamento realizado com sucesso ...", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Algum problema com o pagamento verificar sua conta no paypal.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("NGVL", "onLocationChanged::BEGIN");
        if (mOrigem == null) {
            mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
        }
        mMarkerLocalAtual.setPosition(
                new LatLng(location.getLatitude(), location.getLongitude()));
        Log.d("NGVL", "onLocationChanged::END");
    }

    // Busca a localizacao atual do aparelho.
    private void obterLocalizacao() {
        // Cria uma AsyncTask
            LocalizacaoTask task = new LocalizacaoTask();

            // Executa a task/thread
            task.execute();
    }

    // Busca os links no YouTube em uma nova thread
    private void pesquisarLinks() {
        // Cria uma AsyncTask
        LinkYoutubeTask task = new LinkYoutubeTask();

        // Executa a task/thread
        task.execute();
    }

    public void verificarStatusGPS() {
        Log.d("NGVL", "verificarStatusGPS::BEGIN");
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder locationSettingsRequest =
                new LocationSettingsRequest.Builder();
        locationSettingsRequest.setAlwaysShow(true);
        locationSettingsRequest.addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        locationSettingsRequest.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        obterUltimaLocalizacao();
                        break;
                }
            }
        });
        Log.d("NGVL", "verificarStatusGPS::END");
    }

    private void obterUltimaLocalizacao() {
        Log.d("NGVL", "obterUltimaLocalizacao::BEGIN");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            mTentativas = 0;
            mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
        } else if (mTentativas < 10) {  // vamos tentar obter a última localização 10 vezes
            mTentativas++;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obterUltimaLocalizacao();
                }
            }, 1000); // a cada 1 segundos
        }
        Log.d("NGVL", "obterUltimaLocalizacao::END");
    }

    private class LinkYoutubeTask extends AsyncTask<Void, Void, List<SearchResult>> {

        final String API_KEY = "AIzaSyA8LVmKQhpvKJ_tBG53Bq8JUUenTNb3qAc";

        final String MARCHA_PARA_JESUS = "marcha para jesus";

        /************* BUSCAS NO YOUTUBE *********************/
        /**
         * Global instance of the HTTP transport.
         */
        final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

        /**
         * Global instance of the JSON factory.
         */
        final JsonFactory JSON_FACTORY = new JacksonFactory();

        /**
         * Global instance of the max number of videos we want returned (50 = upper
         * limit per page).
         */
        final long NUMBER_OF_VIDEOS_RETURNED = 25;

        /**
         * Global instance of Youtube object to make all API requests.
         */
        private YouTube youtube;
        private MainActivity context;


        @Override
        protected List<SearchResult> doInBackground(Void... voids) {

            List<SearchResult> searchResultList = null;

            try {
            /*
             * The YouTube object is used to make all API requests. The last
			 * argument is required, but because we don't need anything
			 * initialized when the HttpRequest is initialized, we override the
			 * interface and provide a no-op function.
			 */
                youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-cmdline-search-sample").build();

                YouTube.Search.List search = youtube.search().list("id,snippet");

			/*
             * It is important to set your developer key from the Google
			 * Developer Console for non-authenticated requests (found under the
			 * API Access tab at this link: code.google.com/apis/). This is good
			 * practice and increased your quota.
			 */
                search.setKey(API_KEY);
                search.setQ(MARCHA_PARA_JESUS);

			/*
             * We are only searching for videos (not playlists or channels). If
			 * we were searching for more, we would add them as a string like
			 * this: "video,playlist,channel".
			 */
                search.setType("video");

			/*
             * This method reduces the info returned to only the fields we need
			 * and makes calls more efficient.
			 */
                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                SearchListResponse searchResponse = search.execute();

                searchResultList = searchResponse.getItems();
            } catch (GoogleJsonResponseException e) {
                System.err.println(
                        "There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return searchResultList;
        }

        @Override
        protected void onPostExecute(List<SearchResult> searchResults) {
            super.onPostExecute(searchResults);

            if (searchResults != null) {
                List<VideoEntry> list = new ArrayList<VideoEntry>();

                Iterator<SearchResult> iteratorSearchResults = searchResults.iterator();

                while (iteratorSearchResults.hasNext()) {

                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();

                    // Double checks the kind is video.
                    if (rId.getKind().equals("youtube#video")) {
                        list.add(new VideoEntry(singleVideo.getSnippet().getTitle(), rId.getVideoId()));
                    }
                }
                VIDEO_LIST = new ArrayList<VideoEntry>(Collections.unmodifiableList(list));
            }
        }
    }

    private class LocalizacaoTask extends AsyncTask<Void, Void, LatLng> {

        @Override
        protected LatLng doInBackground(Void... voids) {
            verificarStatusGPS();

            return mOrigem;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);

            if (latLng != null) {
                Double latitude = latLng.latitude;
                Double longitude = latLng.longitude;

                TextView txtLatitude = (TextView) findViewById(R.id.txtLatitude);

                txtLatitude.setText(latitude.toString());

                TextView txtLongitude = (TextView) findViewById(R.id.txtLongitude);

                txtLongitude.setText(longitude.toString());
            }
        }
    }
}
