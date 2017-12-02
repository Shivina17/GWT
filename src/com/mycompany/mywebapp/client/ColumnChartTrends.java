package com.mycompany.mywebapp.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendPosition;

import java.util.HashMap;

public class ColumnChartTrends extends DockLayoutPanel {
    private HashMap<String, Integer> hashMap;
    private HashMap<String, Integer> hashMap2;
    private ColumnChart columnChart;

    public ColumnChartTrends(HashMap<String, Integer> hashMap, HashMap<String, Integer> hashMap2) {
        super(Style.Unit.PX);
        this.hashMap = hashMap;
        this.hashMap2 = hashMap2;
        initialize();
    }

    private void initialize() {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                // Create and attach the chart
                columnChart = new ColumnChart();
               // audioChart = new ColumnChart();
                add(columnChart);
               // add(audioChart);
                draw();
            }
        });
    }

    private void draw() {
        // Prepare the data

        try {

            DataTable colOldData = DataTable.create();
            colOldData.addColumn(ColumnType.STRING, "Customer");
            colOldData.addColumn(ColumnType.NUMBER, "Number of books bought");

            for (String name : hashMap.keySet()) {
                colOldData.addRow(name, hashMap.get(name));
            }

            ColumnChartOptions options = ColumnChartOptions.create();
            options.setTitle("Customers");
            options.setWidth(900);
            options.setHeight(500);
            options.setLegend(Legend.create(LegendPosition.NONE));

            columnChart.draw(colOldData, options);

            Panel customerPanel = new VerticalPanel();
            customerPanel.setPixelSize(900, 600);
            customerPanel.add(new HTML("<b style=\"font-size: 23px; font-style : italic;\">CUSTOMERS WHO BUY THE MOST</b>"));
            customerPanel.add(columnChart);

            ColumnChart newChart =  new ColumnChart();
            DataTable audioData = DataTable.create();
            audioData.addColumn(ColumnType.STRING, "Books");
            audioData.addColumn(ColumnType.NUMBER, "Number of customers that bought it");

            for (String name : hashMap2.keySet()) {
                audioData.addRow(name, hashMap2.get(name));
            }
            ColumnChartOptions options2 = ColumnChartOptions.create();
            options2.setTitle("Books");
            options2.setWidth(900);
            options2.setHeight(500);
            options2.setLegend(Legend.create(LegendPosition.NONE));
            newChart.draw(audioData, options2);

            Panel customerPanel2 = new VerticalPanel();
            customerPanel2.setPixelSize(900, 600);
            customerPanel2.add(new HTML("<b style=\"font-size: 23px; font-style : italic;\">POPULAR AUDIOBOOKS</b>"));
            customerPanel2.add(newChart);

            Panel panel = new VerticalPanel();
            panel.add(customerPanel);
            panel.add(customerPanel2);

            RootPanel.get("infoPanel").add(panel);


        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}