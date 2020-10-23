package view;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import util.kalmanfilter;

public class pcomparisoncontroller {
	
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
    
    @FXML
    private Label avularX;
    @FXML
    private Label avularY;
    @FXML
    private Label standardX;
    @FXML
    private Label standardY;
    
    public  XYChart.Series realXcoords;
    public  XYChart.Series AvularXcoords;
    public  XYChart.Series standardXcoords;
    public  XYChart.Series realYcoords;
    public  XYChart.Series AvularYcoords;
    public  XYChart.Series standardYcoords;
    
    private int iterations;
    private List<Double> avulartempX;
    private List<Double> standardtempX;
    private List<Double> realtempX;
    private List<Double> avulartempY;
    private List<Double> standardtempY;
    private List<Double> realtempY;
	
    @FXML
    private void initialize()
    {
        xAxisX.setLabel("Measurements over time");
        yAxisX.setLabel("Position in X coordinate");
        xAxisX.setAnimated(false);
        
        xAxisY.setLabel("Measurements over time");
        yAxisY.setLabel("Position in Y coordinate");
        xAxisY.setAnimated(false);
        
        realXcoords = new Series<String, Number>();
        AvularXcoords = new Series<String, Number>();
        standardXcoords = new Series<String, Number>();
        realYcoords = new Series<String, Number>();
        AvularYcoords = new Series<String, Number>();
        standardYcoords = new Series<String, Number>();
        
        realXcoords.setName("Real X coordinates");
        AvularXcoords.setName("Avular X estimates");
        standardXcoords.setName("Standard X estimates");

        realYcoords.setName("Real Y coordinates");
        AvularYcoords.setName("Avular Y estimates");
        standardYcoords.setName("Standard Y estimates");
        
        linechartX.setCreateSymbols(false);
        linechartY.setCreateSymbols(false);
        
    }
    
    public void calculate(List<Double> UWBX, List<Double> UWBY, List<Double> UWBX_sensor, List<Double> UWBY_sensor, List<Double> OFX_sensor, List<Double> OFY_sensor, List<Double> IMUX_sensor, List<Double> IMUY_sensor, int Pmode, int iterations, double KFUWBvalue_internal, double KFOFvalue_internal, double KFIMUvalue_internal, double KFcovar_internal, double sampletime)
	{
    	kalmanfilter kf = new kalmanfilter(KFUWBvalue_internal,KFOFvalue_internal,KFIMUvalue_internal,KFcovar_internal,sampletime);
    	
    	avulartempX = new ArrayList();
    	standardtempX = new ArrayList();
    	realtempX = new ArrayList();
    	avulartempY = new ArrayList();
    	standardtempY = new ArrayList();
    	realtempY = new ArrayList();
    	this.iterations = iterations;
    	
        
    	for (int i = 0; i<iterations; i++)
	        {
	        	double[][] avularupdate = kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i), 1);
//	        	System.out.println(UWBX_sensor.get(i) + " " + UWBY_sensor.get(i) + " " + OFX_sensor.get(i) + " " + OFY_sensor.get(i) + " " + IMUX_sensor.get(i) + " " + IMUY_sensor.get(i));
//	        	kf = null;
//	        	System.out.println(kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i), 1)[0][0]);
//	        	kf.clear();
//	        	System.out.println(kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i), 1)[0][0]);
	        	
	        	String measurement = Integer.toString(i);
	        	
	        	int avularxupdate = (int) Math.round(avularupdate[0][0]*100000);
	        	AvularXcoords.getData().add(new XYChart.Data(measurement, avularxupdate));
	        	int avularyupdate = (int) Math.round(avularupdate[1][0]*100000);	        	
	        	AvularYcoords.getData().add(new XYChart.Data(measurement, avularyupdate));
	        	
	        	avulartempX.add(avularupdate[0][0]);
	        	avulartempY.add(avularupdate[1][0]);
	        	        	
	        	int realxupdate = (int) (UWBX.get(i)*100000);
	        	realXcoords.getData().add(new XYChart.Data(measurement, realxupdate));
	        	int realyupdate = (int) (UWBY.get(i)*100000);
	        	realYcoords.getData().add(new XYChart.Data(measurement, realyupdate));	
	        	
	        	realtempX.add(UWBX.get(i));
	        	realtempY.add(UWBY.get(i));
	        } 
    	
    	
    	kf = null;
    	kf = new kalmanfilter(KFUWBvalue_internal,KFOFvalue_internal,KFIMUvalue_internal,KFcovar_internal,sampletime);
    	for (int i = 0; i<iterations; i++)
    	{
    		double[][] standardupdate = kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i), 0);
    		//System.out.println(UWBX_sensor.get(i) + " " + UWBY_sensor.get(i) + " " + OFX_sensor.get(i) + " " + OFY_sensor.get(i) + " " + IMUX_sensor.get(i) + " " + IMUY_sensor.get(i));
    		
    		String measurement = Integer.toString(i);
        	int standardxupdate = (int) Math.round(standardupdate[0][0]*100000);
        	standardXcoords.getData().add(new XYChart.Data(measurement, standardxupdate));
        	int standardyupdate = (int) Math.round(standardupdate[1][0]*100000);	        	
        	standardYcoords.getData().add(new XYChart.Data(measurement, standardyupdate));
        	
        	standardtempX.add(standardupdate[0][0]);
        	standardtempY.add(standardupdate[1][0]);
        	//System.out.println(standardupdate[0][0]);
    	}
    	
	        linechartX.getData().add(realXcoords);
	        linechartX.getData().add(AvularXcoords);
	        linechartX.getData().add(standardXcoords);

	        linechartY.getData().add(realYcoords);
	        linechartY.getData().add(AvularYcoords);
	        linechartY.getData().add(standardYcoords);
	        
	        squaredresidual();

	}
    
    private void squaredresidual()
    {
    	double avularresidualX = 0;
    	double standardresidualX = 0;
    	double avularresidualY = 0;
    	double standardresidualY = 0;
    	for (int i = 0; i<iterations; i++)
    	{
    		avularresidualX += (avulartempX.get(i)-realtempX.get(i))*(avulartempX.get(i)-realtempX.get(i));
    		if (avularresidualX < 0)
    		{
    			System.out.println("ERROR");
    		}
    		avularresidualY += (avulartempY.get(i)-realtempY.get(i))*(avulartempY.get(i)-realtempY.get(i));
    		standardresidualX += (standardtempX.get(i)-realtempX.get(i))*(standardtempX.get(i)-realtempX.get(i));
    		standardresidualY += (standardtempY.get(i)-realtempY.get(i))*(standardtempY.get(i)-realtempY.get(i));	
    	}
    	avularX.setText(String.valueOf(avularresidualX));
    	avularY.setText(String.valueOf(avularresidualY));
    	standardX.setText(String.valueOf(standardresidualX));
    	standardY.setText(String.valueOf(standardresidualY));
    	System.out.println(avularresidualX);
    	System.out.println(avularresidualY);
    	System.out.println(standardresidualX);
    	System.out.println(standardresidualY);
    	
    	String text = "";
    	
    	if (avularresidualX > standardresidualX)
    	{
    		text = text + "standard X is better and ";
    	}
    	else 
    	{
    		text = text + "avular X is better and ";
    	}  	
    	if (avularresidualY > standardresidualY)
    	{
    		text = text + "standard Y is better";    		
    	}
    	else
    	{
    		text = text + "avular Y is better";
    	}
    	System.out.println(text);
    	
    }
	
	public void cleardata()
	{
    	if (realXcoords != null)
    	{
    		realXcoords.getData().clear();
    		realYcoords.getData().clear();
    		AvularXcoords.getData().clear();
    		AvularYcoords.getData().clear();
    		standardXcoords.getData().clear();
    		standardYcoords.getData().clear();
    		
    		linechartX.getData().clear();
    		linechartY.getData().clear();
    	}
	}


}
