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

public class GestionAdministradoresActivity extends AppCompatActivity {

    //Variables que se asocian a componentes gráficos
    EditText etNombre, etUsuario, etContrasena;
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
        setContentView(R.layout.activity_gestion_administradores);

        etNombre = (EditText) findViewById(R.id.txtNombre);
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

    private void ejecutarWebServices(String url, final String msg){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GestionAdministradoresActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionAdministradoresActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionAdministradoresActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    public void insertarAdministrador(View view){
        tipo = "1";
        ejecutarWebServices("http://192.168.1.86:80/aled/administradores/androidGestionAdministradoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), "Se insertó el nuevo administrador");
        limpiarCampos();
    }//insertarAdministrador

    public void buscarAdministrador(View view){
        tipo = "2";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/administradores/androidGestionAdministradoresMySql.php?tipo=" + tipo + "&nombre=" + etNombre.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            JSONArray administrador = new JSONArray(new String(responseBody));
                            etNombre.setText(administrador.getJSONObject(0).getString("nombre"));
                            etContrasena.setText(administrador.getJSONObject(0).getString("pass"));
                        }else{
                            Toast.makeText(GestionAdministradoresActivity.this, "Administrador no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionAdministradoresActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//buscarAdministrador

    public void actualizarAdministrador(View view){
        tipo = "3";
        ejecutarWebServices("http://192.168.1.86:80/aled/administradores/androidGestionAdministradoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), "Administrador Modificado");
        limpiarCampos();
    }//actualizarAdminstrador

    public void eliminarAdministrador(View view){
        tipo = "4";
        ejecutarWebServices("http://192.168.1.86:80/aled/administradores/androidGestionAdministradoresMySql.php?tipo=" + tipo +
                "&nombre=" + etNombre.getText().toString() +
                "&usuario=" + etUsuario.getText().toString() + "&contrasena=" + etContrasena.getText().toString(), "Administrador Eliminado");
        limpiarCampos();
    }//eliminarAdministrador

    public void listarAdministradores(View view){
        String caso = "1";
        Intent intent = new Intent(this, ListadoActivity.class);
        intent.putExtra("caso", caso);
        startActivity(intent);
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

    public void limpiarCampos(){
        etUsuario.setText("");
        etNombre.setText("");
        etContrasena.setText("");
        etUsuario.requestFocus();
    }
}