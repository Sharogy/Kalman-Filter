package view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import util.AlertBox;
import util.kalmanfilter;

public class mainviewcontroller {
	
	private Main main;
	private Stage dialogStage;
	
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
    private Label realUWB;
    @FXML
    private Label realOF;
    @FXML
    private Label realIMU;
    
    @FXML
    private TextField positionX;
    @FXML
    private TextField positionY;
    @FXML
    private TextField velocityX;
    @FXML
    private TextField velocityY;
    @FXML
    private TextField accelerateX;
    @FXML
    private TextField accelerateY;
    
    @FXML
    private TextField realUWBvalue;
    @FXML
    private TextField realOFvalue;
    @FXML
    private TextField realIMUvalue;
    @FXML
    private TextField KFUWBvalue;
    @FXML
    private TextField KFOFvalue;
    @FXML
    private TextField KFIMUvalue;
    
    @FXML
    private TextField KFcovarvalue;
    @FXML
    private TextField KFsamplevalue;
    
    @FXML
    private Label iteration;
    @FXML
    private Slider iteration_slider;
    
    
    
    
    @FXML
    private Button calculate;
    

    
    public  XYChart.Series Xcoords;
    public  XYChart.Series realXcoords;
    public  XYChart.Series sensorXcoords;
    public  XYChart.Series Ycoords;
    public  XYChart.Series realYcoords;
    public  XYChart.Series sensorYcoords;

    private List<Double>UWBX = new ArrayList();
    private List<Double>UWBY = new ArrayList();
    private List<Double>OFX = new ArrayList();
    private List<Double>OFY = new ArrayList();
    private List<Double>IMUX = new ArrayList();
    private List<Double>IMUY = new ArrayList();
    
    private List<Double>UWBX_sensor = new ArrayList();
    private List<Double>UWBY_sensor = new ArrayList();
    private List<Double>OFX_sensor = new ArrayList();
    private List<Double>OFY_sensor = new ArrayList();
    private List<Double>IMUX_sensor = new ArrayList();
    private List<Double>IMUY_sensor = new ArrayList();
    
    private double positionX_internal;
	private double velocityX_internal;
	private double accelerateX_internal;
	private double positionY_internal;
	private double velocityY_internal;
	private double accelerateY_internal;
	
	
	
	private double KFsample_internal;
	
	private static int counter = 0;
    
	
	public void setMainApp(Main main) {
    	
        this.main = main;        
        main.getPrimaryStage().setResizable(false);
        dialogStage = main.getPrimaryStage();
             
    }
	
	
	
	@FXML
    private void initialize() {
		
		//dialogStage = main.getPrimaryStage();

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
        
        
        positionX.setText("0.0");
        positionY.setText("0.0");
        velocityX.setText("0.1");
        velocityY.setText("0.1");
        accelerateX.setText("0.1");
        accelerateY.setText("0.05");
        realUWBvalue.setText("0.05");
        realOFvalue.setText("0.05");
        realIMUvalue.setText("0.05");
        KFUWBvalue.setText("80");
        KFOFvalue.setText("8000");
        KFIMUvalue.setText("10");
        KFcovarvalue.setText("10000000000");
        KFsamplevalue.setText("0.02");
        iteration.setText("100");
        iteration_slider.setMax(500);
        iteration_slider.setMin(50);
        iteration_slider.setValue(100);
        iteration_slider.setBlockIncrement(10);
        iteration_slider.setValueChanging(true);
        iteration_slider.setShowTickLabels(true);
        iteration_slider.setShowTickMarks(true);

        iteration_slider.valueProperty().addListener(new ChangeListener<Number>() {
	        public void changed(ObservableValue<? extends Number> ov,
	            Number old_val, Number new_val) {
	        		iteration_slider.setValue((int) Math.round(new_val.doubleValue()));
	                iteration.setText(String.valueOf((int) Math.round(new_val.doubleValue())));
	        }
	    });
        
        //linechartY.getData().add(Ycoords);                

    }
	
	@FXML
	private void calculate() {
		if (counter == 1)
		{
			safety();
		}
		else
		{
				
		if (isInputValid())
		{
			positionX_internal = Double.valueOf(positionX.getText());
			velocityX_internal = Double.valueOf(velocityX.getText());
			accelerateX_internal = Double.valueOf(accelerateX.getText());
			positionY_internal = Double.valueOf(positionY.getText());
			velocityY_internal = Double.valueOf(velocityY.getText());
			accelerateY_internal = Double.valueOf(accelerateY.getText());
			
			KFsample_internal = Double.valueOf(KFsamplevalue.getText());
			
			int iterations = Integer.valueOf(iteration.getText());
			
			double realUWBvalue_internal = Double.valueOf(realUWBvalue.getText());
	        double realOFvalue_internal = Double.valueOf(realOFvalue.getText());
	        double realIMUvalue_internal = Double.valueOf(realIMUvalue.getText());
	        double KFUWBvalue_internal = Double.valueOf(KFUWBvalue.getText());
	        double KfOFvalue_internal = Double.valueOf(KFOFvalue.getText());
	        double KFIMUvalue_internal = Double.valueOf(KFIMUvalue.getText());
	        
	        double KFcovar_internal = Double.valueOf(KFcovarvalue.getText());
	       
	        getOFX(realOFvalue_internal);
	        getOFY(realOFvalue_internal);
	        getUWBX(realUWBvalue_internal);
	        getUWBY(realUWBvalue_internal);
	        getIMUX(realIMUvalue_internal);
	        getIMUY(realIMUvalue_internal);
	        
	        KF(realUWBvalue_internal, realOFvalue_internal, realIMUvalue_internal);
	        kalmanfilter kf = new kalmanfilter(KFUWBvalue_internal,KfOFvalue_internal,KFIMUvalue_internal,KFcovar_internal,0.02);
	        
	        for (int i = 0; i<iterations; i++)
	        {
	        	double[][] update = kf.calculate(UWBX_sensor.get(i), UWBY_sensor.get(i), OFX_sensor.get(i), OFY_sensor.get(i), IMUX_sensor.get(i), IMUY_sensor.get(i));
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
	        	
	        }        
	        linechartX.getData().add(sensorXcoords);
	        linechartX.getData().add(realXcoords);
	        linechartX.getData().add(Xcoords);
	        linechartY.getData().add(sensorYcoords);
	        linechartY.getData().add(realYcoords);
	        linechartY.getData().add(Ycoords);
		}
		counter = 1;
		}
        
	}
	
	private List<Double> getUWBX(double realUWBvalue_internal)
	{
		UWBX.add(positionX_internal);
		UWBX_sensor.add((new NormalDistribution(positionX_internal,realUWBvalue_internal).sample()));
		for (int i = 1; i<500; i++)
		{	
			//NEED FIX
			
			double number = UWBX.get(i-1)+OFX.get(i-1)*KFsample_internal;
			UWBX.add(number);
			//System.out.println(number);
			NormalDistribution ND = new NormalDistribution(number, realUWBvalue_internal);
			UWBX_sensor.add(ND.sample());	
		}	
		return UWBX_sensor;
	}
	
	private List<Double> getUWBY(double realUWBvalue_internal)
	{
		UWBY.add(positionY_internal);
		UWBY_sensor.add((new NormalDistribution(positionY_internal,realUWBvalue_internal).sample()));
		for (int i = 1; i<500; i++)
		{	
			//double number = positionY_internal + velocityY_internal*KFsample_internal*i + 0.5*accelerateY_internal*KFsample_internal*KFsample_internal*i*i;
			//NEED FIX		
			double number = UWBY.get(i-1) + OFY.get(i-1)*KFsample_internal; 
			UWBY.add(number);
			NormalDistribution ND = new NormalDistribution(number, realUWBvalue_internal);
			UWBY_sensor.add(ND.sample());		
		}
		return UWBY_sensor;
		
	}
	
	private List<Double> getOFX(double realOFvalue_internal)
	{
		for (int i = 0; i<500; i++)
		{	
			double number = velocityX_internal+accelerateX_internal*KFsample_internal*i;
			OFX.add(number);
			//System.out.println(number);
			NormalDistribution ND = new NormalDistribution(number, realOFvalue_internal);
			OFX_sensor.add(ND.sample());
		    //System.out.println(OFX_sensor.get(i));		
		}
		return OFX_sensor;
	}
	
	private List<Double> getOFY(double realOFvalue_internal)
	{
		for (int i = 0; i<500; i++)
		{	
			
			double number = velocityY_internal+accelerateY_internal*KFsample_internal*i;
			OFY.add(number);
			NormalDistribution ND = new NormalDistribution(number, realOFvalue_internal);
			OFY_sensor.add(ND.sample());
		//	System.out.println(FOY.get(i));		
		}
		return OFY_sensor;
	}
	
	private List<Double> getIMUX(double realIMUvalue_internal)
	{
		for (int i = 0; i<500; i++)
		{	
			double number = accelerateX_internal;
			IMUX.add(number);
			NormalDistribution ND = new NormalDistribution(number, realIMUvalue_internal);
			IMUX_sensor.add(ND.sample());
			//System.out.println(IMUX.get(i));		
		}
		return IMUX;
	}
	
	private List<Double> getIMUY(double realIMUvalue_internal)
	{
		for (int i = 0; i<500; i++)
		{	
			double number = accelerateY_internal;
			IMUY.add(number);
			NormalDistribution ND = new NormalDistribution(number, realIMUvalue_internal);
			IMUY_sensor.add(ND.sample());
			//System.out.println(IMUY.get(i));		
		}
		return IMUY;
	}
	
	private int KF (double UWB, double OF, double IMU)
	{				
		return 0;
	}
	
	private boolean isInputValid() {
        String errorMessage = "";
        if (realUWBvalue.getText() == null || realUWBvalue.getText().length() == 0) {
            errorMessage += "UWB is not valid number!\n"; 
        } else {
            try {
                Double.parseDouble(realUWBvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "UWB is not valid number (must be a double)!\n"; 
            }
        }
        if (realOFvalue.getText() == null || realOFvalue.getText().length() == 0) {
            errorMessage += "OF is not valid number!\n"; 
        } else {
            try {
            	Double.parseDouble(realOFvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "OF is not valid number (must be a double)!\n"; 
            }
        }
        if (realIMUvalue.getText() == null || realIMUvalue.getText().length() == 0) {
            errorMessage += "IMU is not valid number!\n"; 
        } else {
            try {
            	Double.parseDouble(realIMUvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "IMU is not valid number (must be a double)!\n"; 
            }
        }
        if (KFUWBvalue.getText() == null || KFUWBvalue.getText().length() == 0) {
            errorMessage += "KF UWB is not valid number!\n"; 
        } else {
            try {
                Double.parseDouble(KFUWBvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "KF UWB is not valid number (must be a double)!\n"; 
            }
        }
        if (KFOFvalue.getText() == null || KFOFvalue.getText().length() == 0) {
            errorMessage += "KF OF is not valid number!\n"; 
        } else {
            try {
            	Double.parseDouble(KFOFvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "KF OF is not valid number (must be a double)!\n"; 
            }
        }
        if (KFIMUvalue.getText() == null || KFIMUvalue.getText().length() == 0) {
            errorMessage += "KF IMU is not valid number!\n"; 
        } else {
            try {
            	Double.parseDouble(KFIMUvalue.getText());
            } catch (NumberFormatException e) {
                errorMessage += "KF IMU is not valid number (must be a double)!\n"; 
            }
        }
                
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;
        }
    }
	
	public void safety()
    {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.initOwner(main.getPrimaryStage());
    	alert.setTitle("Data overload");
    	alert.setHeaderText("Please clear the data first");
    	alert.setContentText("Click the Clear Data button");
    	alert.showAndWait();
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
    	counter = 0;
    }
	

}
