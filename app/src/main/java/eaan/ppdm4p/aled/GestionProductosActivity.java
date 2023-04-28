package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class GestionProductosActivity extends AppCompatActivity {

    //Componentes que se asocian a componentes gráficos
    EditText etCodigo, etDescripcion, etPrecio;

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
        setContentView(R.layout.activity_gestion_productos);

        etCodigo = (EditText) findViewById(R.id.txtCodigo);
        etDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        etPrecio = (EditText) findViewById(R.id.txtPrecio);
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
                Toast.makeText(GestionProductosActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionProductosActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionProductosActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void insertarProducto(View view){
        tipo = "1";
        ejecutarWebServices("http://192.168.1.86:80/aled/productos/androidGestionProductosMySql.php?tipo=" + tipo +
                "&codigo=" + etCodigo.getText().toString() +
                "&descripcion=" + etDescripcion.getText().toString() +
                "&precio=" + etPrecio.getText().toString(), "Se insertó el nuevo producto");
        limpiarCampos();
    } //insertarVendedor

    public void buscarProducto(View view){
        tipo = "2";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/productos/androidGestionProductosMySql.php?tipo=" + tipo + "&codigo=" + etCodigo.getText().toString() +
                "&descripcion=" + etDescripcion.getText().toString() + "&precio=" + etPrecio.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            JSONArray vendedor = new JSONArray(new String(responseBody));
                            etDescripcion.setText(vendedor.getJSONObject(0).getString("descripcion"));
                            etPrecio.setText(vendedor.getJSONObject(0).getString("precio"));
                        }else{
                            Toast.makeText(GestionProductosActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionProductosActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//buscarProducto

    public void actualizarProducto(View view){
        tipo = "3";
        ejecutarWebServices("http://192.168.1.86:80/aled/productos/androidGestionProductosMySql.php?tipo=" + tipo +
                "&codigo=" + etCodigo.getText().toString() + "&descripcion=" + etDescripcion.getText().toString() + "&precio=" +
                etPrecio.getText().toString(), "Producto Modificado");
        limpiarCampos();
    }//actualizarVendedor

    public void eliminarVendedor(View view){
        tipo = "4";
        ejecutarWebServices("http://192.168.1.86:80/aled/productos/androidGestionProductosMySql.php?tipo=" + tipo +
                "&codigo=" + etCodigo.getText().toString() + "&descripcion=" + etDescripcion.getText().toString() + "&precio=" +
                etPrecio.getText().toString(), "Producto Eliminado");
        limpiarCampos();
    }//eliminarVendedor

    public void listarVendedores(View view){
        String caso = "4";
        Intent intent = new Intent(this, ListadoActivity.class);
        intent.putExtra("caso", caso);
        startActivity(intent);
    }//listarVendedores

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
        etCodigo.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        etCodigo.requestFocus();
    }
}
