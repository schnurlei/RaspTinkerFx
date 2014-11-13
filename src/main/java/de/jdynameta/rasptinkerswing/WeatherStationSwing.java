/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinkerswing;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import de.jdynameta.rasptinkerfx.WeatherStation;
import de.jdynameta.rasptinkerfx.model.TinkerForgeConnection;
import eu.hansolo.steelseries.extras.Altimeter;
import eu.hansolo.steelseries.gauges.DisplayRectangular;
import eu.hansolo.steelseries.gauges.Lcd;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.tools.ColorDef;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.PopupMenu;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author rainer
 */
public class WeatherStationSwing extends  JPanel
{
	private TinkerForgeConnection connection;
	private	final Linear linear = new Linear();
	private final Altimeter altimeter = new Altimeter();
	private final DisplayRectangular displayRectangular = new DisplayRectangular();
	
	public WeatherStationSwing()
	{
		init();
		this.connection = new TinkerForgeConnection();
		addBarometer("jZW",linear,altimeter, displayRectangular);
		connection.connect("localhost", 4223);
	}

	private void addBarometer(String uid, Linear linear, Altimeter altimeter, DisplayRectangular displayRectangular) {
		
		BrickletBarometer barometer =  this.connection.addBarometer(uid, 1000);
        barometer.addAirPressureListener(airPressure -> {
			final double pressure = airPressure / 1000.0 ;
			displayRectangular.setValue(pressure);
			try
			{	//todo execute in thread
				final double calculatedTemp = barometer.getChipTemperature()/ 100.0;
				linear.setValue(calculatedTemp);
			} catch (TimeoutException | NotConnectedException ex)
			{
				Logger.getLogger(WeatherStation.class.getName()).log(Level.SEVERE, null, ex);
			}
		});	
        barometer.addAltitudeListener(altitude -> {
			final double altiConverted = altitude / 100.0;
			altimeter.setValue(altiConverted);
		});	
	
	}
	
	 public void init() 
	 {
	    this.setLayout(new GridBagLayout());
	    
	    TitledBorder titledBoder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Title");
	    titledBoder.setTitleJustification(TitledBorder.LEADING);
	    titledBoder.setTitlePosition(TitledBorder.TOP);
	
		displayRectangular.setLcdUnitString("C°");
		displayRectangular.setLcdUnitStringVisible(true);
		
		GridBagConstraints constr = new GridBagConstraints(0, 0, 1, 2,
													1.0, 1.0, GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0,0,0,0),0, 0);
		linear.setTitle("Temperatur");
		linear.setLcdDecimals(1);
		linear.setValueCoupled(true);
		linear.setValue(30.8);
		linear.setMaxMeasuredValueVisible(true);
		linear.setThresholdVisible(false);
		this.add(linear, constr);
		constr.gridx =1;
		constr.gridheight =1;
		
		this.add(altimeter, constr);
		constr.gridy =1;
		this.add(displayRectangular, constr);

		this.add(new JSeparator(SwingConstants.HORIZONTAL));
	    	    
	  }
		
			  
	  public static void main(String[] args) 
	  {
//			UIManager.put("control", new Color(195, 220, 247));

			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			    if ("Nimbus".equals(info.getName())) {
			        try {
						UIManager.setLookAndFeel(info.getClassName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			        break;
			    }
			}
			
		  
		    JFrame frame = new JFrame();
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(new WeatherStationSwing());
		    frame.setSize(500, 300);
		    frame.setVisible(true);
		}


}
