package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Time;

import cz.msebera.android.httpclient.Header;

public class ListadoActivity extends AppCompatActivity {

    /*
    * Se define los casos para listar
    * caso 1: Listar administradores
    * caso 2: Listar vendedores
    * caso 4: Listar productos
    * caso 5: Listar ventas
    * caso 6: Listar clientes
    * caso 7: Listar Telefonos
    * */
    String caso;
    TextView tvLista;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        tvLista = (TextView) findViewById(R.id.txtLista);

        caso = getIntent().getStringExtra("caso");

        AsyncHttpClient client = new AsyncHttpClient();

        if(caso.equals("1")){
            intent = new Intent(this, GestionAdministradoresActivity.class);
            tvLista.setText("Lista de Administradores\n");
            client.get("http://192.168.1.86:80/aled/administradores/androidConsultaAdministradoresMySql.php",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try {
                                    String resultado = new String(responseBody);
                                    if (!resultado.equals("0")){
                                        int i = 0;
                                        JSONArray administrador = new JSONArray(new String(responseBody));
                                        while (i<administrador.length()){
                                            tvLista.setText(tvLista.getText() + "\nUsuario: " + administrador.getJSONObject(i).getString("usuario"));
                                            tvLista.setText(tvLista.getText() + "\nNombre: " + administrador.getJSONObject(i).getString("nombre"));
                                            tvLista.setText(tvLista.getText() + "\nContraseña: " + administrador.getJSONObject(i).getString("pass"));
                                            tvLista.setText(tvLista.getText() + "\n");
                                            i++;
                                        }
                                    }else{
                                        Toast.makeText(ListadoActivity.this, "No hay administradores registrador", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }else if(caso.equals("2")){
            intent = new Intent(this, GestionVendedoresActivity.class);
            tvLista.setText("Lista de Vendedores\n");
            client.get("http://192.168.1.86:80/aled/vendedores/androidConsultaVendedoresMySql.php",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try {
                                    String resultado = new String(responseBody);
                                    if (!resultado.equals("0")){
                                        int i = 0;
                                        JSONArray vendedor = new JSONArray(new String(responseBody));
                                        while (i<vendedor.length()){
                                            tvLista.setText(tvLista.getText() + "\nUsuario: " + vendedor.getJSONObject(i).getString("usuario"));
                                            tvLista.setText(tvLista.getText() + "\nNombre: " + vendedor.getJSONObject(i).getString("nombre"));
                                            tvLista.setText(tvLista.getText() + "\nCorreo: " + vendedor.getJSONObject(i).getString("correo"));
                                            tvLista.setText(tvLista.getText() + "\nRFC: " + vendedor.getJSONObject(i).getString("RFC"));
                                            tvLista.setText(tvLista.getText() + "\nContraseña: " + vendedor.getJSONObject(i).getString("pass"));
                                            tvLista.setText(tvLista.getText() + "\n");
                                            i++;
                                        }
                                    }else{
                                        Toast.makeText(ListadoActivity.this, "No hay vendedores registrador", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }else if(caso.equals("4")){
            intent = new Intent(this, GestionProductosActivity.class);
            tvLista.setText("Lista de Productos\n");
            client.get("http://192.168.1.86:80/aled/productos/androidConsultaProductosMySql.php",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try {
                                    String resultado = new String(responseBody);
                                    if (!resultado.equals("0")){
                                        int i = 0;
                                        JSONArray direccion = new JSONArray(new String(responseBody));
                                        while (i<direccion.length()){
                                            tvLista.setText(tvLista.getText() + "\nCodigo: " + direccion.getJSONObject(i).getString("codigo"));
                                            tvLista.setText(tvLista.getText() + "\nDescripcion: " + direccion.getJSONObject(i).getString("descripcion"));
                                            tvLista.setText(tvLista.getText() + "\nPrecio: " + direccion.getJSONObject(i).getString("precio"));
                                            tvLista.setText(tvLista.getText() + "\n");
                                            i++;
                                        }
                                    }else{
                                        Toast.makeText(ListadoActivity.this, "No hay productos registrados", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }else if(caso.equals("6")){
            intent = new Intent(this, GestionClientesActivity.class);
            tvLista.setText("Lista de Clientes\n");
            client.get("http://192.168.1.86:80/aled/clientes/androidConsultaClientesMySql.php",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try {
                                    String resultado = new String(responseBody);
                                    if (!resultado.equals("0")){
                                        int i = 0;
                                        JSONArray cliente = new JSONArray(new String(responseBody));
                                        while (i<cliente.length()){
                                            tvLista.setText(tvLista.getText() + "\nNombre: " + cliente.getJSONObject(i).getString("nombre"));
                                            tvLista.setText(tvLista.getText() + "\nCorreo: " + cliente.getJSONObject(i).getString("correo"));
                                            tvLista.setText(tvLista.getText() + "\nCalle: " + cliente.getJSONObject(i).getString("calle"));
                                            tvLista.setText(tvLista.getText() + "\nNumero: " + cliente.getJSONObject(i).getString("numero"));
                                            tvLista.setText(tvLista.getText() + "\nColonia: " + cliente.getJSONObject(i).getString("colonia"));
                                            tvLista.setText(tvLista.getText() + "\nMunicipio: " + cliente.getJSONObject(i).getString("municipio"));
                                            tvLista.setText(tvLista.getText() + "\nEstado: " + cliente.getJSONObject(i).getString("estado"));
                                            tvLista.setText(tvLista.getText() + "\n");
                                            i++;
                                        }
                                    }else{
                                        Toast.makeText(ListadoActivity.this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }else if(caso.equals("5")){
            intent = new Intent(this, GestionVentasActivity.class);
            tvLista.setText("Lista de Ventas\n");
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
                                            if( j == -1){
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
                                        Toast.makeText(ListadoActivity.this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }else if(caso.equals("7")){
            intent = new Intent(this, GestionTelefonosActivity.class);
            tvLista.setText("Lista de Telefonos\n");
            client.get("http://192.168.1.86:80/aled/telefonos/androidConsultaTelefonosMySql.php",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode == 200){
                                try {
                                    String resultado = new String(responseBody);
                                    if (!resultado.equals("0")){
                                        int i = 0;
                                        JSONArray telefonos = new JSONArray(new String(responseBody));
                                        while (i<telefonos.length()){
                                            tvLista.setText(tvLista.getText() + "\nCliente: " + telefonos.getJSONObject(i).getString("correo"));
                                            tvLista.setText(tvLista.getText() + "\nTelefono: " + telefonos.getJSONObject(i).getString("numero"));
                                            tvLista.setText(tvLista.getText() + "\n");
                                            i++;
                                        }
                                    }else{
                                        Toast.makeText(ListadoActivity.this, "Telefonos registrados", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(ListadoActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
        }
    }//onCreate

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

    public void regresar(View view){
        startActivity(intent);
        finish();
    }

}
