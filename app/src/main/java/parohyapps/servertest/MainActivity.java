package parohyapps.servertest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Reciever{

    private TextView textOut = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOut = (TextView) findViewById(R.id.tw_out);

        ConnectThread connectThread = new ConnectThread(this);
        connectThread.execute();


    }

    @Override
    public void recieve(String in) {
        if(textOut != null){
            JSONArray jsonArray = null;
            try{
                jsonArray = new JSONArray(in);
                if(jsonArray.length() > 0){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    textOut.setText(jsonObject.getString("email"));
                }
            }
            catch (Exception e){
                Log.d("MainActivity","JSON error: "+e.toString());
            }
        }
    }

    private class ConnectThread extends AsyncTask<String,Void,String>{

        private Reciever reciever;

        public ConnectThread(Reciever re){
            reciever = re;
        }


        @Override
        protected String doInBackground(String... params) {

            InputStream in = null;
            String out = null;
            //connect
            try {
                URL url = new URL("http://192.168.1.4/serverTestBackend/getUser.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (Exception e) {
                Log.d("MainActivity","Connect error: "+e.toString());
            }

            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new InputStreamReader(in,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null){
                    Log.d("MainActivity","Appending: "+line);
                    sb.append(line+"\n");
                }
                in.close();
                out = sb.toString();
                Log.d("MainActivity","Append result: "+out);
            }
            catch (Exception e){
                Log.d("MainActivity","Reader error: "+e.toString());
            }

            //parse JSON
            /*JSONArray jsonArray = null;
            try{
                jsonArray = new JSONArray(out);
                if(jsonArray.length() > 0){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    textOut.setText(jsonObject.getString("email"));
                }
            }
            catch (Exception e){
                Log.d("MainActivity","JSON error: "+e.toString());
            }*/

            return out;
        }

        @Override
        protected void onPostExecute(String s) {
            if(reciever != null){
                reciever.recieve(s);
            }
        }


    }
}
