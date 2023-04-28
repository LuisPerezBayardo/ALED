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

public class GestionVendedoresActivity extends AppCompatActivity {

    //Componentes que se asocian a componentes gráficos
    EditText etNombre, etRFC, etCorreo, etUsuario, etContrasena;

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
        setContentView(R.layout.activity_gestion_vendedores);

        etNombre = (EditText) findViewById(R.id.txtNombre);
        etRFC = (EditText) findViewById(R.id.txtRFC);
        etCorreo = (EditText) findViewById(R.id.txtCorreo);
        etUsuario = (EditText) findViewById(R.id.txtUsuario);
        etContrasena = (EditText) findViewById(R.id.txtContrasena);

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
                Toast.makeText(GestionVendedoresActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionVendedoresActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionVendedoresActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void insertarVendedor(View view){
        tipo = "1";
        ejecutarWebServices("http://192.168.1.86:80/aled/vendedores/androidGestionVendedoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&rfc=" + etRFC.getText().toString() +
                "&correo=" + etCorreo.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() +
                "&contrasena=" + etContrasena.getText().toString(), "Se insertó el nuevo vendedor");
        limpiarCampos();
    } //insertarVendedor

    public void buscarVendedor(View view){
        tipo = "2";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/vendedores/androidGestionVendedoresMySql.php?tipo=" + tipo + "&nombre=" + etNombre.getText().toString() +
                        "&rfc=" + etRFC.getText().toString() + "&correo=" + etCorreo.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            JSONArray vendedor = new JSONArray(new String(responseBody));
                            etNombre.setText(vendedor.getJSONObject(0).getString("nombre"));
                            etRFC.setText(vendedor.getJSONObject(0).getString("RFC"));
                            etCorreo.setText(vendedor.getJSONObject(0).getString("correo"));
                            etContrasena.setText(vendedor.getJSONObject(0).getString("pass"));
                        }else{
                            Toast.makeText(GestionVendedoresActivity.this, "Vendedor no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionVendedoresActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//buscarVendedor

    public void actualizarVendedor(View view){
        tipo = "3";
        ejecutarWebServices("http://192.168.1.86:80/aled/vendedores/androidGestionVendedoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() + "&rfc=" + etRFC.getText().toString() + "&correo=" + etCorreo.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), "Vendedor Modificado");
        limpiarCampos();
    }//actualizarVendedor

    public void eliminarVendedor(View view){
        tipo = "4";
        ejecutarWebServices("http://192.168.1.86:80/aled/vendedores/androidGestionVendedoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() + "&rfc=" + etRFC.getText().toString() + "&correo=" + etCorreo.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), "Vendedor Eliminado");
        limpiarCampos();
    }//eliminarVendedor

    public void listarVendedores(View view){
        String caso = "2";
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
        etNombre.setText("");
        etRFC.setText("");
        etContrasena.setText("");
        etCorreo.setText("");
        etUsuario.setText("");
        etUsuario.requestFocus();
    }
}
