package de.jdynameta.rasptinker.fx;

import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherStation extends Application
{
	private TinkerForgeConnection connection;
    public XYChart.Series seriesTemp = new XYChart.Series();
    public XYChart.Series seriesLuftdruck = new XYChart.Series();
    public XYChart.Series seriesAltitude = new XYChart.Series();

	public WeatherStation()
	{
		seriesTemp.setName("Temp");
        seriesLuftdruck.setName("Luftdruck");
        seriesAltitude.setName("Altitude");
		this.connection = new TinkerForgeConnection();
		addBarometer("jZW",seriesLuftdruck,seriesAltitude, seriesTemp);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Tinkerforge Connect");

		BorderPane border = new BorderPane();
		border.setTop(createTopPane(primaryStage));

        final VBox box = new VBox();
        final ObservableList<Node> boxChildren = box.getChildren();
        boxChildren.add(createLineChart("Temp", seriesTemp));
        boxChildren.add(createLineChart("Luftdruck", seriesLuftdruck));
        boxChildren.add(createLineChart("Altitude", seriesAltitude));
		border.setCenter(box);

//		Text scenetitle = new Text("Welcome");
//		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
//		grid.add(scenetitle, 0, 0, 2, 1);


		Scene scene = new Scene(border, 300, 275);
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	private Pane createTopPane(Stage primaryStage) {
		
		Button connectBtn = new Button("Connect");
		connectBtn.setOnAction((ActionEvent e) ->
		{
			JavaFxUtil.createConnectDialog(primaryStage, this.connection);
		});
		Button disconnectBtn = new Button("Disconnect");
		disconnectBtn.setOnAction((ActionEvent e) ->
		{
			this.connection.disconnect();
		});
		HBox hbBtn = new HBox(10, connectBtn, disconnectBtn);
		
		return hbBtn;
	}
	
	private void addBarometer(String uid, XYChart.Series airPressureSeries, XYChart.Series altitudeSeries, XYChart.Series temperaturSeries) {
		
		BrickletBarometer barometer =  this.connection.addBarometer(uid, 1000);
        barometer.addAirPressureListener(airPressure -> {
			final double pressure = airPressure / 1000.0 ;
			Platform.runLater(() ->
			{
				final XYChart.Data data = new XYChart.Data(new Date(), pressure);
				airPressureSeries.getData().add(data);
				try
				{	//todo execute in thread
					final double calculatedTemp = barometer.getChipTemperature()/ 10.0;
					final XYChart.Data tempData = new XYChart.Data(new Date(), calculatedTemp);
					temperaturSeries.getData().add(tempData);
				} catch (TimeoutException | NotConnectedException ex)
				{
					Logger.getLogger(WeatherStation.class.getName()).log(Level.SEVERE, null, ex);
				}

			});
		});	
        barometer.addAltitudeListener(altitude -> {
			final double altiConverted = altitude / 100.0;
			Platform.runLater(() ->
			{
				final XYChart.Data data = new XYChart.Data(new Date(), altiConverted);
				altitudeSeries.getData().add(data);
			});
		});	
	
	}



   private LineChart createLineChart(final String chartName,final XYChart.Series series ){
        final DateAxis dateAxis = new DateAxis();
        dateAxis.setLabel("Time");
        final NumberAxis yAxis = new NumberAxis();

        final LineChart<Date, Number> lineChart = new LineChart<>(dateAxis, yAxis);
        lineChart.setTitle(chartName);
        lineChart.getData().add(series);

        return lineChart;
    }
	
	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		launch(args);
	}
}
