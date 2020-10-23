package view;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import util.kalmanfilter;

public class xyestimatecontroller {
	
	@FXML
    public LineChart<String, Integer> linechartX;
	@FXML
    public LineChart<String, Integer> linechartY;

    @FXML
    private CategoryAxis xAxisX;
    @FXML
    private CategoryAxis xAxisY;
    
    @FXML
    private NumberAxis yAxisX;
    @FXML
    private NumberAxis yAxisY;
    
    public  XYChart.Series Xcoords;
    public  XYChart.Series realXcoords;
    public  XYChart.Series sensorXcoords;
    public  XYChart.Series Ycoords;
    public  XYChart.Series realYcoords;
    public  XYChart.Series sensorYcoords;
    
    @FXML
    private void initialize()
    {
        xAxisX.setLabel("Measurements over time");
        yAxisX.setLabel("Position in X coordinate");
        xAxisX.setAnimated(false);
        
        xAxisY.setLabel("Measurements over time");
        yAxisY.setLabel("Position in Y coordinate");
        xAxisY.setAnimated(false);
        
        Xcoords = new Series<String, Number>();
        realXcoords = new Series<String, Number>();
        sensorXcoords = new Series<String, Number>();
        Ycoords = new Series<String, Number>();
        realYcoords = new Series<String, Number>();
        sensorYcoords = new Series<String, Number>();
        Xcoords.setName("Estimated X coordinates");
        realXcoords.setName("Real X coordinates");
        sensorXcoords.setName("Sensor X coordinates");
        Ycoords.setName("Estimated Y coordinates");
        realYcoords.setName("Real Y coordinates");
        sensorYcoords.setName("sensor Y coordinates");
        
        linechartX.setCreateSymbols(false);
        linechartY.setCreateSymbols(false);
        
    }
    
    public void calculate(List<Double> UWBX, List<Double> UWBY, List<Double> UWBX_sensor, List<Double> UWBY_sensor, List<Double> OFX_sensor, List<Double> OFY_sensor, List<Double> IMUX_sensor, List<Double> IMUY_sensor, int Pmode, int iterations, double KFUWBvalue_internal, double KFOFvalue_internal, double KFIMUvalue_internal, double KFcovar_internal, double sampletime)
    {
    	
    	kalmanfilter kf = new kalmanfilter(KFUWBvalue_internal,KFOFvalue_internal,KFIMUvalue_internal,KFcovar_internal,sampletime);
        
    	for (int i = 0; i<iterations; i++)
	        //for (int i = 0; i<10; i++)
	        {
	        	double[][] update = kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i), Pmode);
	        	//System.out.println(UWBX.get(i) + " " + OFX.get(i) + " " + IMUX.get(i));
	        	
	        	String measurement = Integer.toString(i);
	        	
	        	int xupdate = (int) Math.round(update[0][0]*1000);
	        	Xcoords.getData().add(new XYChart.Data(measurement, xupdate));
	        	int yupdate = (int) Math.round(update[1][0]*1000);	        	
	        	Ycoords.getData().add(new XYChart.Data(measurement, yupdate));
	        	
	        	int realxupdate = (int) (UWBX.get(i)*1000);
	        	//System.out.println(realxupdate);
	        	realXcoords.getData().add(new XYChart.Data(measurement, realxupdate));
	        	int realyupdate = (int) (UWBY.get(i)*1000);
	        	realYcoords.getData().add(new XYChart.Data(measurement, realyupdate));
	        	
	        	int sensorxupdate = (int) (UWBX_sensor.get(i)*1000);
	        	sensorXcoords.getData().add(new XYChart.Data(measurement, sensorxupdate));
	        	
	        	int sensoryupdate = (int) (UWBY_sensor.get(i)*1000);
	        	sensorYcoords.getData().add(new XYChart.Data(measurement, sensoryupdate));
	        	//System.out.println(update[0][0] + "  " + UWBX.get(i));
	        }        
	        linechartX.getData().add(sensorXcoords);
	        linechartX.getData().add(realXcoords);
	        linechartX.getData().add(Xcoords);
	        linechartY.getData().add(sensorYcoords);
	        linechartY.getData().add(realYcoords);
	        linechartY.getData().add(Ycoords);
    }
    
    public void cleardata()
    {
    	if (Xcoords != null)
    	{
    		Xcoords.getData().clear();
    		Ycoords.getData().clear();
    		realXcoords.getData().clear();
    		realYcoords.getData().clear();
    		sensorXcoords.getData().clear();
    		sensorYcoords.getData().clear();
    		
    		linechartX.getData().clear();
    		linechartY.getData().clear();
    	}
    }

}
