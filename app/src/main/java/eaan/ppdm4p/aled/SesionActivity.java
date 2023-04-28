package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SesionActivity extends AppCompatActivity {

    TextView tvBienvenida;
    String usuario, nivel;
    LinearLayout linear1a, linear1b, linear2a, linear2b;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);

        tvBienvenida = (TextView) findViewById(R.id.txtBienvenidoSesion);
        linear1a = (LinearLayout) findViewById(R.id.ly1a);
        linear1b = (LinearLayout) findViewById(R.id.ly1b);
        linear2a = (LinearLayout) findViewById(R.id.ly2a);
        linear2b = (LinearLayout) findViewById(R.id.ly2b);

        SharedPreferences sharedPreferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        usuario = sharedPreferences.getString("usuario", "");
        //String nivel = sharedPreferences.getString("nivel", "");

        if(sharedPreferences.getString("nivel", "").equals("1")){
            nivel = "Administrador";
        }else if(sharedPreferences.getString("nivel", "").equals("2")){
            nivel = "Vendedor";
            linear1a.setVisibility(LinearLayout.GONE);
            linear1b.setVisibility(LinearLayout.GONE);
            linear2a.setVisibility(LinearLayout.GONE);
            linear2b.setVisibility(LinearLayout.GONE);
        }

        tvBienvenida.setText(tvBienvenida.getText().toString() + " " + nivel + " " + usuario + "!");
    }

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

    public void gestionAdministradores(View view){
        intent = new Intent(this, GestionAdministradoresActivity.class);
        startActivity(intent);
    }

    public void gestionVendedores(View view){
        intent = new Intent(this, GestionVendedoresActivity.class);
        startActivity(intent);
    }

    public void ventasMes(View view){
        intent = new Intent(this, VentasMesActivity.class);
        startActivity(intent);
    }

    public void gestionProductos(View view){
        intent = new Intent(this, GestionProductosActivity.class);
        startActivity(intent);
    }

    public void gestionVentas(View view){
        intent = new Intent(this, GestionVentasActivity.class);
        startActivity(intent);
    }

    public void gestionClientes(View view){
        intent = new Intent(this, GestionClientesActivity.class);
        startActivity(intent);
    }

    public void gestionTelefonos(View view){
        intent = new Intent(this, GestionTelefonosActivity.class);
        startActivity(intent);
    }
}