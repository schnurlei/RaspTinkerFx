/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.model.weather;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rainer
 */
public abstract class WeatherStationModel
{
	private final List<MeasureListener> measureListeners;
	private final List<ErrorListener> errorListeners;
	private final List<ConnectionListener> connectionListeners;
	private final List<TiltListener> tiltListeners;

	public WeatherStationModel()
	{
		this.measureListeners = new ArrayList<>();
		this.errorListeners = new ArrayList<>();
		this.connectionListeners = new ArrayList<>();
		this.tiltListeners = new ArrayList<>();
	}
	
	public void addMeasureListener(MeasureListener aListener) {
		
		this.measureListeners.add(aListener);
	}
	
	public void addErrorListener(ErrorListener aListener) {
		
		this.errorListeners.add(aListener);
	}

	public void addConnectionListener(ConnectionListener aListener) {
		
		this.connectionListeners.add(aListener);
	}

	public void addTiltListener(TiltListener aListener) {
		
		this.tiltListeners.add(aListener);
	}

	protected void notifyMeasureChange(Measurement aMeasurement, BigDecimal value) {

		this.measureListeners.stream().forEach((listener) ->
		{
			listener.changed(aMeasurement, value);
		});
	}

	protected void notifyErrorOccured(Measurement aMeasurement, Exception ex) {

		this.errorListeners.stream().forEach((listener) ->
		{
			listener.error(aMeasurement, ex);
		});
	}
	
	protected void notifyConnectionChanged(Measurement aMeasurement, ConnectionListener.State state) {

		this.connectionListeners.stream().forEach((listener) ->
		{
			listener.changed(aMeasurement, state);
		});
	}

	protected void notifyTiltStateChange(TiltListener.State state) {

		this.tiltListeners.stream().forEach((listener) ->
		{
			listener.changed(state);
		});
	}


	public static BigDecimal decimal(int aValue) {
		
		return decimal(aValue, 0);
	}

	public static BigDecimal decimal1(double aValue) {
		
		return decimal(aValue, 1);
	}

	
	public static BigDecimal decimal(double aValue, int scale) {
		
		return new BigDecimal(aValue).setScale(scale, RoundingMode.HALF_DOWN);
	}

}
