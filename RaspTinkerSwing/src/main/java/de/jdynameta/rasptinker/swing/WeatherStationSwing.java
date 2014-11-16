package de.jdynameta.rasptinker.swing;

import de.jdynameta.rasptinker.model.weather.Measurement;
import de.jdynameta.rasptinker.model.weather.TiltListener;
import de.jdynameta.rasptinker.model.weather.WeatherStationModelTinkerforge;
import eu.hansolo.steelseries.extras.Altimeter;
import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.DisplayRectangular;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.Orientation;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author rainer
 */
public class WeatherStationSwing extends JPanel
{

	private final WeatherStationModelTinkerforge stationModel;
	private final Linear thermometerView = new Linear();
	private final Altimeter altimeterView = new Altimeter();
	private final DisplayRectangular illuminanceView = new DisplayRectangular();
	private final Radial barometerView = new Radial();
	private final Radial hygrometerView = new Radial();

	private final Map<Measurement, AbstractGauge> measure2GaugeMap = new HashMap();

	public WeatherStationSwing()
	{
		this.stationModel = new WeatherStationModelTinkerforge("jZW", "meo", "nBd", "jaN");
		measure2GaugeMap.put(Measurement.AirTemperature, thermometerView);
		measure2GaugeMap.put(Measurement.Altitude, altimeterView);
		measure2GaugeMap.put(Measurement.Illuminace, illuminanceView);
		measure2GaugeMap.put(Measurement.AirPressure, barometerView);
		measure2GaugeMap.put(Measurement.Humidity, hygrometerView);

		initViews();
		measure2GaugeMap.entrySet().stream().forEach((entry) ->
		{
			initGauge(entry.getKey(), entry.getValue());
		});

		initModelListener();
		initViewListener();
		initUi();
		stationModel.connect("localhost", 4223);
	}

	private void initViews()
	{

		illuminanceView.setLcdUnitString(Measurement.Illuminace.getUnit().getUnitString());
		illuminanceView.setLcdUnitStringVisible(true);

		thermometerView.setLcdDecimals(1);
		thermometerView.setValueCoupled(true);
		thermometerView.setMaxMeasuredValueVisible(true);
		thermometerView.setThresholdVisible(false);
		thermometerView.setOrientation(Orientation.VERTICAL);
	}

	private void initViewListener()
	{
		measure2GaugeMap.entrySet().stream().forEach((entry) ->
		{
			entry.getValue().addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getButton() == MouseEvent.BUTTON1)
					{
						openDiagramm(entry.getKey());
					}
				}
			});
		});
	}

	private void initGauge(Measurement aMeasurment, AbstractGauge aGauge)
	{

		aGauge.setMinValue(aMeasurment.getMinValue().doubleValue());
		aGauge.setMaxValue(aMeasurment.getMaxValue().doubleValue());
		aGauge.setUnitString(aMeasurment.getUnit().getUnitString());
		aGauge.setTitle(aMeasurment.getTitle());
		aGauge.getModel().setMinValue(aMeasurment.getMinValue().doubleValue());
		aGauge.setValue(0);
	}

	private void initModelListener()
	{

		this.stationModel.addMeasureListener((aMeasurement, value) ->
		{
			AbstractGauge gauge = measure2GaugeMap.get(aMeasurement);
			gauge.setValue(value.doubleValue());
		});

		this.stationModel.addTiltListener((state) ->
		{
			if (state == TiltListener.State.TILT_STATE_CLOSED
					|| state == TiltListener.State.TILT_STATE_CLOSED_VIBRATING)
			{
				measure2GaugeMap.entrySet().stream().forEach((entry) ->
				{
					entry.getValue().setBackgroundColor(BackgroundColor.RED);
					entry.getValue().setLedColor(LedColor.RED);
				});

			}
		});
	}

	private void openDiagramm(final Measurement measurement) {
		
		JFrame frame = new JFrame("Swing and JavaFX");
        frame.setSize(300, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		XYSeries series = new XYSeries("Chart data");
		series.add(1.0, 400.2);
        series.add(5.0, 294.1);
        series.add(4.0, 100.0);
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		JFreeChart chart = ChartFactory.createLineChart(measurement.getTitle(), measurement.getUnit().getUnitString(), "Zeit", dataset);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		frame.getContentPane().add(chartPanel);
		this.stationModel.addMeasureListener((aMeasurement, value) ->
		{
			if (measurement == aMeasurement) {
				dataset.addValue(value.doubleValue(),"Werte", new Date());
			}
		});

	}
	
	
	 
	
	private void initUi()
	{
		JPanel gaugePnl = new JPanel(new GridLayout(1, 3, 5, 5));
		JPanel gaugePnl1 = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel gaugePnl2 = new JPanel(new GridLayout(2, 1, 5, 5));
		JPanel gaugePnl3 = new JPanel(new GridLayout(2, 1, 5, 5));
		gaugePnl.add(gaugePnl1);
		gaugePnl.add(gaugePnl2);
		gaugePnl.add(gaugePnl3);
		gaugePnl1.add(measure2GaugeMap.get(Measurement.AirTemperature));
		gaugePnl2.add(measure2GaugeMap.get(Measurement.Altitude));
		gaugePnl2.add(measure2GaugeMap.get(Measurement.AirPressure));
		gaugePnl3.add(measure2GaugeMap.get(Measurement.Illuminace));
		gaugePnl3.add(measure2GaugeMap.get(Measurement.Humidity));
		this.setLayout(new BorderLayout());
		this.add(gaugePnl, BorderLayout.CENTER);

	}

	public static void main(String[] args)
	{
//			UIManager.put("control", new Color(195, 220, 247));

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		{
			if ("Nimbus".equals(info.getName()))
			{
				try
				{
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(new WeatherStationSwing());
				frame.setSize(500, 300);
				frame.setVisible(true);
			}
		});
	}

}
