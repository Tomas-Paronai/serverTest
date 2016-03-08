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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textOut = (TextView) findViewById(R.id.tw_out);
        String out = "";

        ConnectThread connectThread = new ConnectThread(textOut);
        connectThread.execute();

    }

    private class ConnectThread extends AsyncTask<String,Void,String>{

        private TextView textOut;
        private boolean once = false;
        public ConnectThread(TextView textOut){
            this.textOut = textOut;
        }

        @Override
        protected String doInBackground(String... params) {
            String out = "";
            //if(!once){
                //connect to db and get response
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://192.168.1.4/serverTestBackend/getUser.php");
                HttpResponse httpResponse = null;
                HttpEntity httpEntity = null;
                InputStream is = null;
                try {
                    httpResponse = httpClient.execute(httpPost);
                    httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } catch (IOException e) {
                    Log.d("MainActivity","Response error: "+e.toString());
                }

                //convert input from server
                BufferedReader reader = null;
                try{
                    reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null){
                        Log.d("MainActivity","Appending: "+line);
                        sb.append(line+"\n");
                    }
                    is.close();
                    out = sb.toString();
                    Log.d("MainActivity","Append result: "+out);
                }
                catch (Exception e){
                    Log.d("MainActivity","Reader error: "+e.toString());
                }

                //parse JSON
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(out);
                    if(jsonArray.length() > 0){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                        textOut.setText(jsonObject.getString("email"));
                    }
                }
                catch (Exception e){
                    Log.d("MainActivity","JSON error: "+e.toString());
                }
                once = true;
           // }

            return null;
        }
    }
}
