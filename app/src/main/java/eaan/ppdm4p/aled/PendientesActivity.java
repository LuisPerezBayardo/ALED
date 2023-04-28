package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class PendientesActivity extends AppCompatActivity {

    //Variables a asociar con los componentes gráficos
    EditText etId;
    TextView tvLista;

    AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes);

        //Asociacion con componentes gráficos
        etId = (EditText) findViewById(R.id.txtId);
        tvLista = (TextView) findViewById(R.id.txtLista);

        actualizarPendientes();
    }
    //Asociar el menu principal con el activity
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.itmAcercaDe:
                Intent acerca = new Intent(this, AcercaDeActivity.class);
                startActivity(acerca);
                break;
            case R.id.itmCerrarSesion:
                cerrarSesion();
                break;
            case R.id.itmPendientes:
                Intent pendientes = new Intent(this, PendientesActivity.class);
                startActivity(pendientes);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ejecutarWebServices(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(PendientesActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PendientesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(PendientesActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void pagado(View view){
        String tipo = "3";

        ejecutarWebServices("http://192.168.1.86:80/aled/ventas/androidConsultaVentasPendientesMySql.php?tipo="+ tipo +
                "&id="+ etId.getText().toString(), "Marcó la venta como pagada y se actualizaron las ventas del mes");
        etId.setText("");
        etId.requestFocus();
        actualizarPendientes();
    }//pagado

    //Servicio para listar ventas pendientes
    public void actualizarPendientes(){
        tvLista.setText("Lista de Pendientes\n");
        client.get("http://192.168.1.86:80/aled/ventas/androidConsultaVentasPendientesMySql.php",
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
                                        if( j == -1){
                                            tvLista.setText(tvLista.getText() + "\nID de venta: " + venta.getJSONObject(i).getString("id"));
                                            tvLista.setText(tvLista.getText() + "\nCliente: " + venta.getJSONObject(i).getString("correo"));
                                            //Checa si es a credito o no
                                            if(venta.getJSONObject(i).getInt("credito") == 0){
                                                tvLista.setText(tvLista.getText() + "\nVenta no a credito");
                                            }else {
                                                tvLista.setText(tvLista.getText() + "\nVenta a credito");
                                            }
                                            tvLista.setText(tvLista.getText() + "\nDias de credito: " + venta.getJSONObject(i).getString("diasCredito"));
                                            tvLista.setText(tvLista.getText() + "\nFecha: " + venta.getJSONObject(i).getString("dia") + "-" + venta.getJSONObject(i).getString("mes") + "-" + venta.getJSONObject(i).getString("anio"));
                                            tvLista.setText(tvLista.getText() + "\nTotal: " + venta.getJSONObject(i).getString("total"));
                                            tvLista.setText(tvLista.getText() + "\nMonto: " + venta.getJSONObject(i).getString("monto"));
                                            tvLista.setText(tvLista.getText() + "\nEstado de la venta: " + venta.getJSONObject(i).getString("statusVenta"));
                                            tvLista.setText(tvLista.getText() + "\nVendedor: " + venta.getJSONObject(i).getString("vendedor"));
                                            tvLista.setText(tvLista.getText() + "\nProducto: " + venta.getJSONObject(i).getString("descripcion"));
                                            tvLista.setText(tvLista.getText() + "\nCantidad: " + venta.getJSONObject(i).getString("cantidad"));
                                        }else{
                                            if(venta.getJSONObject(i).getString("idVenta").equals(venta.getJSONObject(j).getString("idVenta"))){
                                                tvLista.setText(tvLista.getText() + "\nProducto: " + venta.getJSONObject(i).getString("descripcion"));
                                                tvLista.setText(tvLista.getText() + "\nCantidad: " + venta.getJSONObject(i).getString("cantidad"));
                                            }else {
                                                tvLista.setText(tvLista.getText() + "\n");
                                                tvLista.setText(tvLista.getText() + "\nID de venta: " + venta.getJSONObject(i).getString("id"));
                                                tvLista.setText(tvLista.getText() + "\nCliente: " + venta.getJSONObject(i).getString("correo"));
                                                //Checa si es a credito o no
                                                if(venta.getJSONObject(i).getInt("credito") == 0){
                                                    tvLista.setText(tvLista.getText() + "\nVenta no a credito");
                                                }else {
                                                    tvLista.setText(tvLista.getText() + "\nVenta a credito");
                                                }
                                                tvLista.setText(tvLista.getText() + "\nDias de credito: " + venta.getJSONObject(i).getString("diasCredito"));
                                                tvLista.setText(tvLista.getText() + "\nFecha: " + venta.getJSONObject(i).getString("dia") + "-" + venta.getJSONObject(i).getString("mes") + "-" + venta.getJSONObject(i).getString("anio"));
                                                tvLista.setText(tvLista.getText() + "\nTotal: " + venta.getJSONObject(i).getString("total"));
                                                tvLista.setText(tvLista.getText() + "\nMonto: " + venta.getJSONObject(i).getString("monto"));
                                                tvLista.setText(tvLista.getText() + "\nEstado de la venta: " + venta.getJSONObject(i).getString("statusVenta"));
                                                tvLista.setText(tvLista.getText() + "\nVendedor: " + venta.getJSONObject(i).getString("vendedor"));
                                                tvLista.setText(tvLista.getText() + "\nProducto: " + venta.getJSONObject(i).getString("descripcion"));
                                                tvLista.setText(tvLista.getText() + "\nCantidad: " + venta.getJSONObject(i).getString("cantidad"));
                                            }
                                        }
                                        j++;
                                        i++;
                                    }
                                }else{
                                    Toast.makeText(PendientesActivity.this, "No hay pendientes", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(PendientesActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
    }

    public void cerrarSesion(){
        SharedPreferences sharedPreferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent logout = new Intent(this, LoginActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logout);
        finish();
    }
}
