package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
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

import cz.msebera.android.httpclient.Header;
import eaan.ppdm4p.aled.MODELO.Usuario;

public class LoginActivity extends AppCompatActivity {

    EditText etUsu, etContra;
    //CheckBox cbRecor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsu = (EditText) findViewById(R.id.txtUsuario);
        etContra = (EditText) findViewById(R.id.txtContrasena);
    }

    public void ingresar(View view){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/androidIniciarSesionMySql.php?usuario=" + etUsu.getText().toString() +
                "&contra=" + etContra.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    try {
                        String nivel = new String(responseBody);
                        if ( !nivel.equals("0")){
                            if( nivel.equals("1")){
                                Toast.makeText(LoginActivity.this, "Administrador!", Toast.LENGTH_SHORT).show();
                            }else if( nivel.equals("2") ){
                                Toast.makeText(LoginActivity.this, "Vendedor!", Toast.LENGTH_SHORT).show();
                            }

                            Usuario u = new Usuario(etUsu.getText().toString().trim(), etContra.getText().toString().trim(), nivel ,true);
                            guardarPreferencias(u);

                            Intent intent = new Intent(LoginActivity.this, SesionActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }catch ( Exception e ){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void guardarPreferencias(Usuario u){
        SharedPreferences sharedPreferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", u.getUser());
        editor.putString("contrasena", u.getPass());
        editor.putString("nivel", u.getNivel());
        editor.putBoolean("registrado", u.isRegistrado());
        editor.apply();
    }
}