
package fanghao.example.com.keepaccounts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.xutils.DbManager;
import org.xutils.db.table.DbModel;
import org.xutils.x;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import fanghao.example.com.keepaccounts.entity.Acount;
import fanghao.example.com.keepaccounts.listviewitems.BarChartItem;
import fanghao.example.com.keepaccounts.listviewitems.ChartItem;
import fanghao.example.com.keepaccounts.listviewitems.LineChartItem;
import fanghao.example.com.keepaccounts.listviewitems.PieChartItem;
import fanghao.example.com.keepaccounts.notimportant.DemoBase;
import lecho.lib.hellocharts.model.PointValue;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 *
 * @author Philipp Jahoda
 */
public class ListViewMultiChartActivity extends DemoBase {
    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("myAcounts")
                    //.setDbDir(new File("/data/data/fanghao.example.com.keepaccounts"))
            .setDbVersion(1)
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // TODO: ...
                    // db.addColumn(...);
                    // db.dropTable(...);
                    // ...
                }
            });
    ListView lv;
    LineChartItem lineChartItem;
    BarChartItem barChartItem;
    PieChartItem pieChartItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.status_bar_color);
        setTitle("记账统计");
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_listview_chart);

        lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 3 items
        for (int i = 0; i < 3; i++) {

            if(i % 3 == 0) {
                list.add(new LineChartItem(generateDataLine(i + 1), getApplicationContext()));
            } else if(i % 3 == 1) {
                list.add(new BarChartItem(generateDataBar(i + 1), getApplicationContext()));
            } else if(i % 3 == 2) {
                pieChartItem=new PieChartItem(generateDataPie(i + 1), getApplicationContext());
                list.add(pieChartItem);
            }
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }
    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.actionSave) {
           PieChartItem map= (PieChartItem) lv.getItemAtPosition(2);
            lv.getItemAtPosition(0);
            saveToPath("title" + System.currentTimeMillis(), "");
            Toast.makeText(getApplicationContext(), "d", Toast.LENGTH_SHORT).show();
        }
        return true;
    }*/
    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(int cnt) {
        Calendar c = Calendar.getInstance();
        String date=c.get(Calendar.YEAR)+"年";
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            DbManager db = x.getDb(daoConfig);
            try {
                DbModel dbModel =db.selector(Acount.class).select("sum(figure) as sum").where("date", "like", date + (i + 1) + "月%").and("type", "=", 1).findFirst();

               /* int j = 0;
                for (int t =0; t < acountList.size(); t++) {
                     if(acountList.get(t).getFigure()>max)
                        max=acountList.get(t).getFigure();
                    j++;
                }*/
                /*while (cursor.moveToNext()) {
                    values.add(new PointValue(j,Float.parseFloat(cursor.getString(1))));
                    if(Float.parseFloat(cursor.getString(1))>max)
                        max=Float.parseFloat(cursor.getString(1));
                    j++;
                }*/
                if(dbModel.getDataMap().get("sum")!=null){
                    e1.add(new Entry(Float.parseFloat(dbModel.getDataMap().get("sum")), i));
                }
            }catch (Throwable e){

            }
            //e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }

        LineDataSet d1 = new LineDataSet(e1, "收入统计");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        
        /*ArrayList<Entry> e2 = new ArrayList<Entry>();
        for (int i = 0; i < 12; i++) {
            e2.add(new Entry(e1.get(i).getVal() - 30, i));
        }*/

        /*LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleSize(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);*/

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);
        /*sets.add(d2);*/

        LineData cd = new LineData(getMonths(), sets);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateDataBar(int cnt) {
        Calendar c = Calendar.getInstance();
        String date=c.get(Calendar.YEAR)+"年";

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            DbManager db = x.getDb(daoConfig);
            try {
                DbModel dbModel =db.selector(Acount.class).select("sum(figure) as sum").where("date", "like", date + (i + 1) + "月%").and("type", "=", 0).findFirst();

               /* int j = 0;
                for (int t =0; t < acountList.size(); t++) {
                     if(acountList.get(t).getFigure()>max)
                        max=acountList.get(t).getFigure();
                    j++;
                }*/
                /*while (cursor.moveToNext()) {
                    values.add(new PointValue(j,Float.parseFloat(cursor.getString(1))));
                    if(Float.parseFloat(cursor.getString(1))>max)
                        max=Float.parseFloat(cursor.getString(1));
                    j++;
                }*/
                if(dbModel.getDataMap().get("sum")!=null){
                    entries.add(new BarEntry(Float.parseFloat(dbModel.getDataMap().get("sum")), i));
                }
            }catch (Throwable e){

            }

        }

        BarDataSet d = new BarDataSet(entries, "花费统计");
        d.setBarSpacePercent(20f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(getMonths(), d);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(int cnt) {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        DbManager db = x.getDb(daoConfig);
        ArrayList<String> dataType=new ArrayList<>();
        try {
            List<DbModel> dbModels =db.selector(Acount.class).select("sum(figure) as sum","category").groupBy("category").where("type","=",1).findAll();
            /*if(dbModel.getDataMap().get("sum")!=null){
                entries.add(new BarEntry(Float.parseFloat(dbModel.getDataMap().get("sum")), i));
            }*/
            for (int i = 0; i < dbModels.size(); i++) {
                entries.add(new Entry(Float.parseFloat(dbModels.get(i).getDataMap().get("sum")), i));
                dataType.add(dbModels.get(i).getDataMap().get("category"));
                //entries.add(new Entry((int) (Math.random() * 70) + 30, i));
            }
        }catch (Throwable e){

        }


        PieDataSet d = new PieDataSet(entries, "花费分布");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(dataType, d);
        return cd;
    }

    private ArrayList<String> getQuarters() {

        ArrayList<String> q = new ArrayList<String>();
        q.add("一般消费");
        q.add("聚餐");
        q.add("学习");
        q.add("网购");
        q.add("实体店购物");
        q.add("交通费");
        q.add("社交");
        q.add("零食");
        q.add("电话费");
        q.add("礼物");
        q.add("旅游");
        return q;
    }

    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }


}
