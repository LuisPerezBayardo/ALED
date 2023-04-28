package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class GestionTelefonosActivity extends AppCompatActivity {

    //Variables que se asocioan con los componentes graficos
    EditText etTelefono;
    Spinner spClientes;
    TextView tvLista;

    //Arreglos de cadena para adaptar con los spinner
    private String[] clientes;

    String tipo;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_telefonos);

        //Asociacion de componentes
        etTelefono = (EditText) findViewById(R.id.txtNumero);
        spClientes = (Spinner) findViewById(R.id.spClientes);
        tvLista = (TextView) findViewById(R.id.txtLista);

        final AsyncHttpClient client = new AsyncHttpClient();

        //Direccion donde se encuentra el archivo para obtener el corre de los clientes registrados en la base de datos
        String urlClientes = "http://192.168.1.86:80/aled/clientes/androidConsultaClientesMySql.php";

        //Obtiene los clientes actualmente registrados
        client.get(urlClientes, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try {
                        String resultado = new String(responseBody);
                        if( !resultado.equals("0")){
                            int i = 0;
                            JSONArray jsonArray = new JSONArray(new String(responseBody));
                            //Define el tamaño del arreglo
                            clientes = new String[jsonArray.length()];
                            while (i<jsonArray.length()){
                                //Llena posicion por posicion el arreglo con el correo del cliente
                                clientes[i] = jsonArray.getJSONObject(i).getString("correo");
                                i++;
                            }
                            //Adapta el arreglo con el spinner
                            adapter = new ArrayAdapter<String>(GestionTelefonosActivity.this, android.R.layout.simple_spinner_dropdown_item, clientes);
                            spClientes.setAdapter(adapter);
                        }else {
                            Toast.makeText(GestionTelefonosActivity.this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionTelefonosActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });//cliente
    }

    //Metodos para el menu principal que contiene el cierre de sesion
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

    //Servicio para conectarse al servidor
    private void ejecutarWebServices(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GestionTelefonosActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionTelefonosActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionTelefonosActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void insertarTelefono(View view){
        tipo = "1";
        ejecutarWebServices("http://192.168.1.86:80/aled/telefonos/androidGestionTelefonosMySql.php?tipo=" + tipo +
                "&numero=" + etTelefono.getText().toString() +
                "&cliente=" + spClientes.getSelectedItem().toString(), "Se insertó el nuevo telefono");
        limpiarCampos();
    }//insertarTelefono

    public void buscarTelefono(View view){
        tipo = "2";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/telefonos/androidGestionTelefonosMySql.php?tipo=" + tipo +
                "&numero=" + etTelefono.getText().toString() +
                "&cliente=" + spClientes.getSelectedItem().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            int i = 0, n = 1;
                            JSONArray telefonos = new JSONArray(new String(responseBody));
                            tvLista.setText("Lista de telefonos del usuario " + telefonos.getJSONObject(i).getString("correo"));
                            while (i < telefonos.length()){
                                tvLista.setText(tvLista.getText() + "\nTelefono " + n + ": " + telefonos.getJSONObject(i).getString("numero"));
                                tvLista.setText(tvLista.getText() + "\n");
                                i++;
                                n++;
                            }
                        }else{
                            Toast.makeText(GestionTelefonosActivity.this, "No se ncontraron telefonos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionTelefonosActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//buscarTelefono

    public void eliminarTelefono(View view){
        tipo = "4";
        ejecutarWebServices("http://192.168.1.86:80/aled/telefonos/androidGestionTelefonosMySql.php?tipo=" + tipo +
                "&numero=" + etTelefono.getText().toString() +
                "&cliente=" + spClientes.getSelectedItem().toString(), "Telefono Eliminado");
        limpiarCampos();
    }//eliminarTelefono

    public void listarVentas(View view){
        String caso = "7";
        Intent intent = new Intent(this, ListadoActivity.class);
        intent.putExtra("caso", caso);
        startActivity(intent);
    }//listarVentas

    //Metodo para cerrar la sesion
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

    public void limpiarCampos(){
        etTelefono.setText("");
        etTelefono.requestFocus();
    }
}
