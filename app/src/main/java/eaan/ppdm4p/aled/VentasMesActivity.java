package eaan.ppdm4p.aled;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VentasMesActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    //private String[]months = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    //private double[]sale = new double[]{0,0,0,0,0,0,0,0,0,0,0,0};
    private int[]colors = new int[]{Color.BLUE, Color.RED, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.YELLOW};
    AsyncHttpClient client = new AsyncHttpClient();

    private String[] meses = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private double[]totalVendido = new double[]{0,0,0,0,0,0,0,0,0,0,0,0};
    private double[]totalPagado = new double[]{0,0,0,0,0,0,0,0,0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_mes);

        barChart = (BarChart) findViewById(R.id.barChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);

        //Direccion donde se encuentra el archivo para obtener la descripcion de los productos registrados en la base de datos
        String urlProductos = "http://192.168.1.86:80/aled/ventas/androidConsultaVentasDelMesMySql.php";

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
                            //Define el tamaño de los arreglos
                            //meses = new String[jsonArray.length()];
                            //totalPagado = new String[jsonArray.length()];
                            //totalVendido = new String[jsonArray.length()];
                            while (i<jsonArray.length()){
                                //Llena posicion por posicion el arreglo con la descripción del producto
                                //meses[i] = jsonArray.getJSONObject(i).getString("mes");
                                totalVendido[i] = jsonArray.getJSONObject(i).getDouble("vendido");
                                totalPagado[i] = jsonArray.getJSONObject(i).getDouble("pagado");
                                //totalVendido[i] = jsonArray.getJSONObject(i).getString("vendido");
                                i++;
                            }
                            createCharts();
                        }else {
                            Toast.makeText(VentasMesActivity.this, "No hay ventas", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(VentasMesActivity.this, "Sin resultado.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });//client
    }

    //regresa la gráfica personalizada
    private Chart getSameChart(Chart chart, String description, int textColor, int background, int animateY){
        chart.getDescription().setText(description);
        chart.getDescription().setTextSize(15);
        chart.setBackgroundColor(background);
        chart.animateY(animateY);
        legend(chart);
        return chart;
    }

    //Datos dentro de la leyenda
    private void legend(Chart chart){
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        //Se agregan los colores y meses abajo
        ArrayList<LegendEntry>entries = new ArrayList<>();
        for (int i=0; i<meses.length; i++){
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors[i];
            entry.label = meses[i];
            entries.add(entry);
        }
        //se agregan los datos personalizados
        legend.setCustom(entries);
    }//legend

    //Datos dentro de las gráficas
    private ArrayList<BarEntry> getBarEntries(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i=0; i<totalPagado.length; i++){
            //Dentro de BarEntry(x, y)
            entries.add(new BarEntry(i, (float) totalPagado[i]));
        }
        return entries;
    }

    //Datos dentro de las gráficas
    private ArrayList<PieEntry> getPieEntries(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i=0; i<totalVendido.length; i++){
            //Dentro de pieEntry(valores)
            entries.add(new PieEntry((float) totalVendido[i]));
        }
        return entries;
    }

    //Eje x
    private void axisX(XAxis axis){
        //Se adecuan las barras con los meses
        //Se puede especificar de cuanto en cuanto se puede mostrar la grafica
        axis.setGranularityEnabled(true);
        //Poner en la parte de abajo
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Colocar los meses
        axis.setValueFormatter(new IndexAxisValueFormatter(meses));
    }

    //Trabajar eje y, se puede a izquierda o a derecha
    //Izquierdo
    private void axisLeft(YAxis axis){
        //agregar mas espacion en la parte de arriba
        axis.setSpaceTop(30);
        //agrega el valor minimo
        axis.setAxisMinimum(0);
    }

    //Derecho, pero con false para que no aparezca
    private void axisRight(YAxis axis){
        axis.setEnabled(false);
    }

    //creamos graficas
    public void createCharts( ){
        //llama a la grafica de barra
        barChart = (BarChart) getSameChart(barChart, "Pagado", Color.RED, Color.WHITE, 3000);
        //Apareceran las lineas de la grafica
        //barChart.setDrawGridBackground(true);
        //Se le agrega una sombra
        //barChart.setDrawBarShadow(true);

        barChart.setData(getBarData());
        barChart.invalidate();

        //Llama los ejes
        //Primero x
        axisX(barChart.getXAxis());
        axisLeft(barChart.getAxisLeft());
        axisRight(barChart.getAxisRight());

        //personalizar la grafica de pastel
        pieChart = (PieChart) getSameChart(pieChart, "Vendido", Color.GRAY, Color.LTGRAY, 3000);
        //tamaño del circulo
        pieChart.setHoleRadius(10);
        pieChart.setTransparentCircleRadius(12);

        pieChart.setData(getPieData());
        pieChart.invalidate();
        //pieChart.setDrawHoleEnabled(false);

    }

    //agregar los datos dentro de la grafica
    private DataSet getData(DataSet dataSet){
        dataSet.setColors(colors);
        //Color de etiquetas dentro de la grafica de pastel
        dataSet.setValueTextSize(Color.WHITE);
        dataSet.setValueTextSize(10);
        return dataSet;
    }

    //Personalizar los contenidos especificamente de las graficas, tomando los establecidos en getDtata
    private BarData getBarData(){
        BarDataSet barDataSet = (BarDataSet) getData(new BarDataSet(getBarEntries(), ""));
        barDataSet.setBarShadowColor(Color.GRAY);
        BarData barData = new BarData(barDataSet);
        //Ancho de las barras
        barData.setBarWidth(0.45f);
        return barData;
    }

    private PieData getPieData(){
        PieDataSet pieDataSet = (PieDataSet) getData(new PieDataSet(getPieEntries(), ""));
        //Espacion entre partes de grafica
        pieDataSet.setSliceSpace(0);
        //Graficos en porcentaje
        pieDataSet.setValueFormatter(new PercentFormatter());
        return new PieData(pieDataSet);
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
