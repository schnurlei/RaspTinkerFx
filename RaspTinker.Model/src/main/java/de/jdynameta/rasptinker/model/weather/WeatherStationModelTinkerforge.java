package de.jdynameta.rasptinker.model.weather;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletTilt;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import de.jdynameta.rasptinker.model.weather.TiltListener.State;
import java.math.BigDecimal;


public class WeatherStationModelTinkerforge extends WeatherStationModel
{
	private final TinkerForgeConnection connection;

	public WeatherStationModelTinkerforge(String barometerUid, String ambientUid, String humidityUid, String tiltUid)
	{
		this.connection = new TinkerForgeConnection();
		this.addBarometer(barometerUid);
		this.addAmbientLight(ambientUid);
		this.addHumidity(humidityUid);
		this.addTilt(tiltUid);
	}

	public void connect(String host, int port) {
		connection.connect(host, port);
	}
	
	private void addBarometer(String uid) {
		
		BrickletBarometer barometer =  this.connection.addBarometer(uid, 1000);
        barometer.addAirPressureListener(airPressure -> {
			final BigDecimal pressure = decimal1(airPressure).divide(decimal1(1000.0));
			notifyMeasureChange(Measurement.AirPressure, pressure);
			try
			{	//todo execute in thread
				final BigDecimal temperature = decimal1(barometer.getChipTemperature()).divide(decimal1(100.0));
				notifyMeasureChange(Measurement.AirTemperature, temperature);
			} catch (TimeoutException | NotConnectedException ex)
			{
				notifyErrorOccured(Measurement.AirTemperature, ex);
			}
		});	
        barometer.addAltitudeListener(altitude -> {
			final BigDecimal alti = decimal1(altitude).divide(decimal1(100.0));
			notifyMeasureChange(Measurement.Altitude, alti);
		});	
	
	}
	
	private void addAmbientLight(String uid) {
		
		BrickletAmbientLight ambient =  this.connection.addAmbientLight(uid, 1000);
        ambient.addIlluminanceListener(illuminance -> {
			final BigDecimal pressure = decimal(illuminance).divide(decimal(10));
			notifyMeasureChange(Measurement.Illuminace, pressure);
		});	
	}
	
	private void addHumidity(String uid) {
		
		BrickletHumidity bricklet =  this.connection.addHumidity(uid, 1000);
        bricklet.addHumidityListener(humidity -> {
			final BigDecimal humi = decimal(humidity).divide(decimal(10));
			notifyMeasureChange(Measurement.Humidity, humi);
		});	
	}
	
	private void addTilt(String uid) {
		
		BrickletTilt tilt =  this.connection.addTilt(uid, 1000);
		tilt.addTiltStateListener(tiltState -> {
			TiltListener.State state = State.TILT_STATE_OPEN;
			switch(tiltState) {
			   case BrickletTilt.TILT_STATE_CLOSED: state = State.TILT_STATE_CLOSED; 
			   break;
			   case BrickletTilt.TILT_STATE_OPEN: state = State.TILT_STATE_OPEN;
			   break;
			   case BrickletTilt.TILT_STATE_CLOSED_VIBRATING:  state = State.TILT_STATE_CLOSED_VIBRATING; 
			   break;
		   }
			notifyTiltStateChange(state);
		});
	}
	
}
