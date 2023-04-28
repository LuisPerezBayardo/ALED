package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actualizacion();

        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                if(sesionIniciada()){
                    intent = new Intent(MainActivity.this, SesionActivity.class);
                }else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(tarea, 2000);
    }

    public boolean sesionIniciada(){
        SharedPreferences sharedPreferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        return sharedPreferences.getBoolean("registrado", false);
    }















    public void actualizacion(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/ventas/androidConsultaVentasMySql.php",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(statusCode == 200){
                            try {
                                String resultado = new String(responseBody);
                                if (!resultado.equals("0")){
                                    int i = 0, j = -1;
                                    JSONArray venta = new JSONArray(new String(responseBody));
                                    while (i<venta.length()){
                                        int diasCredito=venta.getJSONObject(i).getInt("credito");
                                        if(diasCredito != 0){
                                            int dia=Integer.valueOf(venta.getJSONObject(i).getString("dia"));
                                            int mes=Integer.valueOf(venta.getJSONObject(i).getString("mes"));
                                            int anio=Integer.valueOf(venta.getJSONObject(i).getString("anio"));
                                            Calendar inicio=Calendar.getInstance();
                                            inicio.set(anio, mes, dia);
                                            Calendar actual= Calendar.getInstance();
                                            long act=actual.getTimeInMillis();
                                            long ini=inicio.getTimeInMillis();
                                            int dias=(int)((act-ini)/(1000*60*60*24));
                                            if(dias>diasCredito){

                                            }
                                        }
                                        i++;
                                    }
                                }else{
                                    Toast.makeText(MainActivity.this, "No hay ventas registradas", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
    }
}
