package eaan.ppdm4p.aled;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class GestionVentasActivity extends AppCompatActivity {

    //Variables que se asocian con los componentes gráficos
    EditText etFecha, etCantidad, etDiasCredito, etMonto, etTotal, etVendedor;
    RadioButton rbSi, rbNo;
    Spinner spClientes, spProductos;

    //Varciables para el calendario
    static final int DATE_ID = 0;
    Calendar calendar = Calendar.getInstance();
    private int nYearIni, nMonthIni, nDayIni, sYearIni, sMonthIni, sDayIni;

    //Arreglos de cadena para adaptar con los spinner
    private String[] productos;
    private String[] clientes;

    ArrayAdapter adapter;

    //Variables para validaaciones y mandar a la base de datos
    String tipo, credito, dia, mes, anio;
    double total, cant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_ventas);

        //Asociacion con componenetes gráficos
        etCantidad = (EditText) findViewById(R.id.txtCantidad);
        etDiasCredito = (EditText) findViewById(R.id.txtDiasCredito);
        etMonto = (EditText) findViewById(R.id.txtMonto);
        etTotal = (EditText) findViewById(R.id.txtTotal);
        rbSi = (RadioButton) findViewById(R.id.rbSi);
        rbNo = (RadioButton) findViewById(R.id.rbNo);
        spClientes = (Spinner) findViewById(R.id.spClientes);
        spProductos = (Spinner) findViewById(R.id.spProductos);
        etVendedor = (EditText) findViewById(R.id.txtVendedor);
        etFecha = (EditText) findViewById(R.id.txtFecha);

        //Obtener los datos respectivos para el calendario
        sMonthIni = calendar.get(Calendar.MONTH);
        sDayIni = calendar.get(Calendar.DAY_OF_MONTH);
        sYearIni = calendar.get(Calendar.YEAR);

        //Obtener el usuario en sesion para registrar quien realiza la venta
        SharedPreferences sharedPreferences = getSharedPreferences("user.dat", MODE_PRIVATE);
        etVendedor.setText(sharedPreferences.getString("usuario", ""));

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_ID);
            }
        });

        final AsyncHttpClient client = new AsyncHttpClient();

        //Direccion donde se encuentra el archivo para obtener la descripcion de los productos registrados en la base de datos
        String urlProductos = "http://192.168.1.86:80/aled/productos/androidConsultaProductosMySql.php";

        //Obtener la descripción de los productos que se encuentran actualmente
        client.get(urlProductos, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try {
                        String resultado = new String(responseBody);
                        if( !resultado.equals("0")){
                            int i = 0;
                            JSONArray jsonArray = new JSONArray(new String(responseBody));
                            //Define el tamaño del arreglo
                            productos = new String[jsonArray.length()];
                            while (i<jsonArray.length()){
                                //Llena posicion por posicion el arreglo con la descripción del producto
                                productos[i] = jsonArray.getJSONObject(i).getString("descripcion");
                                i++;
                            }
                            //Adapta el arreglo con el spinner
                            adapter = new ArrayAdapter<String>(GestionVentasActivity.this, android.R.layout.simple_spinner_dropdown_item, productos);
                            spProductos.setAdapter(adapter);
                        }else {
                            Toast.makeText(GestionVentasActivity.this, "No hay productos registrados", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionVentasActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });//cliente

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
                            adapter = new ArrayAdapter<String>(GestionVentasActivity.this, android.R.layout.simple_spinner_dropdown_item, clientes);
                            spClientes.setAdapter(adapter);
                        }else {
                            Toast.makeText(GestionVentasActivity.this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionVentasActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });//cliente

        //Establede el evento para cuando se presione un RadioButton
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgCredito);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int boton = radioGroup.indexOfChild(radioButton);

                switch (boton){
                    case 0:
                        etDiasCredito.setText("15");
                        break;
                    case 1:
                        etDiasCredito.setText("0");
                        break;
                }
            }
        });
    }//on Create

    //Coloca la fecha en el EditText
    private void colocarFecha(){
        dia = String.valueOf(nDayIni);
        mes = String.valueOf(nMonthIni + 1);
        anio = String.valueOf(nYearIni);

        etFecha.setText((nMonthIni + 1) + "-" + + nDayIni + "-" + nYearIni + " ");
    }//colocarFecha

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            nYearIni = year;
            nMonthIni = month;
            nDayIni = dayOfMonth;
            colocarFecha();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id){
        switch (id){
            case DATE_ID:
                return new DatePickerDialog(this, onDateSetListener, sYearIni, sMonthIni, sDayIni);
        }
        return null;
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
                Toast.makeText(GestionVentasActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GestionVentasActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

        };
        RequestQueue requestQueue = Volley.newRequestQueue(GestionVentasActivity.this);
        requestQueue.add(stringRequest);
    }//ejecutarWebService

    //Inserta el registro en la tabla intermedia de muchos a muchos cuando se presiona el carrito
    public void anadirCarrito(View view){
        //Validacion para campos sin lleanar
        if(etTotal.getText().toString().equals("")){
            total = 0;
        }else{
            total = Double.parseDouble(etTotal.getText().toString());
        }
        if(etCantidad.getText().toString().equals("")){
            cant = 0;
        }else{
            cant = Double.parseDouble(etCantidad.getText().toString());
        }

        //Tipo 1 es para hacer INSERT en la base de datos
        tipo = "1";
        //Primero añade en la tabla intermedia todos los registros en la base de datos
        ejecutarWebServices("http://192.168.1.86:80/aled/ventas/androidGestionCarritoMySql.php?tipo=" + tipo + "&cantidad=" + etCantidad.getText().toString() +
                "&producto=" + spProductos.getSelectedItem().toString(), "Añadido al carrito");

        //Tipo 2 es para SELECT en la base de datos
        tipo = "2";
        //Busca el precio del producto en la base de datos para despues multiplicarlo por la cantidad de productos que el cliente solicitó y colocar el total en el EditText correspondiente
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.1.86:80/aled/ventas/androidGestionCarritoMySql.php?tipo=" + tipo + "&cantidad=" + etCantidad.getText().toString() +
                "&producto=" + spProductos.getSelectedItem().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    try{
                        String respuesta = new String(responseBody);
                        if ( !respuesta.equals("0") ){
                            JSONArray producto = new JSONArray(new String(responseBody));
                            //Al totatl anterior le suma el nuevo producto multiplicado por la cantidad
                            total = total + (producto.getJSONObject(0).getDouble("precio") * cant);
                            //Recicla la variable tipo, solo para poner el tipo como totoal en EditText
                            tipo = String.valueOf(total);
                            etTotal.setText(tipo.toString());
                        }else{
                            Toast.makeText(GestionVentasActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(GestionVentasActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }//anadir

    public void insertarVenta(View view){
        //Tipo 1 es para hacer INSERT en la base de datos
        tipo = "1";
        //cuando Credito = 0  es que la venta no se va a realizar a credito
        //cuando Credito = 1 es que la venta se va a realizar a credito
        credito = "0";

        //Valida que tenga llenos los campos requeridos
        if (etFecha.getText().toString().equals("") || etMonto.getText().toString().equals("") || etDiasCredito.getText().toString().equals("") || etTotal.getText().toString().equals("") || etVendedor.getText().toString().equals("")){
            Toast.makeText(this, "Aun hay campos vacios", Toast.LENGTH_LONG).show();
        }else {
            //Validar si es a credito o no, mediante los RadioButton
            if( rbSi.isChecked() ){
                credito = "1";
                //Si no se ingresan los dias de credito, los dias por default son 15
                if(etDiasCredito.getText().toString().equals("")){
                    etDiasCredito.setText("15");
                }
                if(etTotal.getText().toString().equals(etMonto.getText().toString())){
                    Toast.makeText(this, "La venta no es a credito porque el monto es la misma cantidad que el total", Toast.LENGTH_LONG).show();
                }else {
                    ejecutarWebServices("http://192.168.1.86:80/aled/ventas/androidGestionVentasMySql.php?tipo=" + tipo +
                            "&credito=" + credito +
                            "&diascredito=" + etDiasCredito.getText().toString() +
                            "&monto=" + etMonto.getText().toString() +
                            "&total=" + etTotal.getText().toString() +
                            "&dia=" + dia +
                            "&mes=" + mes +
                            "&anio=" + anio +
                            "&vendedor=" + etVendedor.getText().toString() +
                            "&cliente=" + spClientes.getSelectedItem().toString(), "Venta completada");
                    limpiarCampos();
                }
            }else if (rbNo.isChecked()){
                credito = "0";
                etDiasCredito.setText("0");
                if(!etTotal.getText().toString().equals(etMonto.getText().toString())){
                    Toast.makeText(this, "El monto no puede ser menos de la cantidad total si la venta no es a credito", Toast.LENGTH_LONG).show();
                }else {
                    ejecutarWebServices("http://192.168.1.86:80/aled/ventas/androidGestionVentasMySql.php?tipo=" + tipo +
                            "&credito=" + credito +
                            "&diascredito=" + etDiasCredito.getText().toString() +
                            "&monto=" + etMonto.getText().toString() +
                            "&total=" + etTotal.getText().toString() +
                            "&dia=" + dia +
                            "&mes=" + mes +
                            "&anio=" + anio +
                            "&vendedor=" + etVendedor.getText().toString() +
                            "&cliente=" + spClientes.getSelectedItem().toString(), "Venta completada");
                    limpiarCampos();
                }
            }
        }
    }//insertarVenta

    public void listarVentas(View view){
        String caso = "5";
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
        etDiasCredito.setText("");
        etCantidad.setText("");
        etMonto.setText("");
        etTotal.setText("");
        etCantidad.requestFocus();
    }
}