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

public class GestionClientesActivity extends AppCompatActivity {

    //variables que se asocian con los componentes gráficos
    EditText etNombre, etCorreo, etTelefono, etColonia, etNumero, etCalle, etMunicipio;
    Spinner spEstados;
    ArrayAdapter adapter;

    //Arreglo tipo String que recibe el nombre de los estados desde la base de datos mediante un JSONArray
    private String[] estados;

    /*
    * Variable tipo se utiliza para definir el tipo de consulta
    * tipo 1: INSERT
    * tipo 2: SELECT
    * tipo 3: UPDATE
    * tipo 4: DELETE
    * */
    String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_clientes);

        spEstados = (Spinner) findViewById(R.id.spEstados);
        etNombre = (EditText) findViewById(R.id.txtNombre);
        etCorreo = (EditText) findViewById(R.id.txtCorreo);
        etTelefono = (EditText) findViewById(R.id.txtTelefono);
        etCalle = (EditText) findViewById(R.id.txtCalle);
        etNumero = (EditText) findViewById(R.id.txtNumero);
        etColonia = (EditText) findViewById(R.id.txtColonia);
        etMunicipio = (EditText) findViewById(R.id.txtMunicipio);

        AsyncHttpClient client = new AsyncHttpClient();

        //Direccion donde se encuentra el archivo para obtener el nombre de todos los estados registrados en la base de datos
        String url = "http://192.168.1.86:80/aled/direcciones/androidObtenerEstadosMySql.php";

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try {
                        String resultado = new String(responseBody);
                        if( !resultado.equals("0")){
                            int i = 0;
                            JSONArray jsonArray = new JSONArray(new String(responseBody));
                            //Define el tamaño del arreglo
                            estados = new String[jsonArray.length()];
                            while (i<jsonArray.length()){
                                //Llena posicion por posicion el arreglo con el nommbre del estado
                                estados[i] = jsonArray.getJSONObject(i).getString("nombre");
                                i++;
                            }
                            //Adapta el arreglo con el spinner
                            adapter = new ArrayAdapter<String>(GestionClientesActivity.this, android.R.layout.simple_spinner_dropdown_item, estados);
                            spEstados.setAdapter(adapter);
                        }else {
                            Toast.makeText(GestionClientesActivity.this, "No hay estados registrador", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionClientesActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });//cliente
    }//onCreate

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
                Toast.makeText(GestionClientesActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionClientesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionClientesActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void insertarCliente(View view){
        tipo = "1";
        ejecutarWebServices("http://192.168.1.86:80/aled/clientes/androidGestionClientesMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&correo=" + etCorreo.getText().toString() +
                "&telefono=" + etTelefono.getText().toString() +
                "&calle=" + etCalle.getText().toString() +
                "&numero=" + etNumero.getText().toString() +
                "&colonia=" + etColonia.getText().toString() +
                "&municipio=" + etMunicipio.getText().toString() +
                "&estado=" + spEstados.getSelectedItem().toString(), "Se insertó el nuevo cliente");
        limpiarCampos();
    } //insertarClientes

    public void buscarCliente(View view){
        tipo = "2";
        Toast.makeText(GestionClientesActivity.this, "presionando buscar.", Toast.LENGTH_LONG).show();
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/clientes/androidGestionClientesMySql.php?tipo=" + tipo +
                        "&nombre=" + etNombre.getText().toString() +
                        "&correo=" + etCorreo.getText().toString() +
                        "&telefono=" + etTelefono.getText().toString() +
                        "&calle=" + etCalle.getText().toString() +
                        "&numero=" + etNumero.getText().toString() +
                        "&colonia=" + etColonia.getText().toString() +
                        "&municipio=" + etMunicipio.getText().toString() +
                        "&estado=" + spEstados.getSelectedItem().toString(),
                new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            JSONArray cliente = new JSONArray(new String(responseBody));
                            etNombre.setText(cliente.getJSONObject(0).getString("nombre"));
                            etTelefono.setText(cliente.getJSONObject(0).getString("telefono"));
                            etCalle.setText(cliente.getJSONObject(0).getString("calle"));
                            etNumero.setText(cliente.getJSONObject(0).getString("numero"));
                            etColonia.setText(cliente.getJSONObject(0).getString("colonia"));
                            etMunicipio.setText(cliente.getJSONObject(0).getString("municipio"));
                        }else{
                            Toast.makeText(GestionClientesActivity.this, "cliente no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionClientesActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//buscarCliente

    public void actualizarCliente(View view){
        tipo = "3";
        ejecutarWebServices("http://192.168.1.86:80/aled/clientes/androidGestionClientesMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&correo=" + etCorreo.getText().toString() +
                "&telefono=" + etTelefono.getText().toString() +
                "&calle=" + etCalle.getText().toString() +
                "&numero=" + etNumero.getText().toString() +
                "&colonia=" + etColonia.getText().toString() +
                "&municipio=" + etMunicipio.getText().toString() +
                "&estado=" + spEstados.getSelectedItem().toString(), "Cliente modificado");
        limpiarCampos();
    }//actualizarCliente

    public void listarClientes(View view){
        String caso = "6";
        Intent intent = new Intent(this, ListadoActivity.class);
        intent.putExtra("caso", caso);
        startActivity(intent);
    }//listarClientes

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
        etNombre.setText("");
        etCorreo.setText("");
        etTelefono.setText("");
        etCalle.setText("");
        etColonia.setText("");
        etNumero.setText("");
        etMunicipio.setText("");
        etNombre.requestFocus();
    }
}
