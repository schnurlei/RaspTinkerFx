/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.model.weather;

import java.math.BigDecimal;

/**
 *
 * @author rainer
 */
public enum Measurement
{

	AirTemperature(Unit.DegreeCelsius, "Temperatur", decimal(0, 2), decimal(100, 2)),
	Altitude(Unit.Metre, "Meereshöhe", decimal(0, 2), decimal(100, 2)),
	Humidity(Unit.Percent, "Luftfeuchte", decimal(0, 2), decimal(100, 2)),
	AirPressure(Unit.MilliBar, "Luftdruck", decimal(900, 2), decimal(1050, 2)),
	Illuminace(Unit.Lux, "Lichtstärke", decimal(0, 2), decimal(100, 2));

	private final Unit unit;
	private final String title;
	private final BigDecimal minValue;
	private final BigDecimal maxValue;

	private Measurement(Unit anUnit, String aTitle, BigDecimal aMinValue, BigDecimal aMaxValue)
	{
		this.unit = anUnit;
		this.title = aTitle;
		this.minValue = aMinValue;
		this.maxValue = aMaxValue;
	}

	public Unit getUnit()
	{
		return unit;
	}

	public String getTitle()
	{
		return title;
	}

	public BigDecimal getMinValue()
	{
		return minValue;
	}

	public BigDecimal getMaxValue()
	{
		return maxValue;
	}
	
	private static BigDecimal decimal(double aValue, int scale) {
		
		return new BigDecimal(aValue).setScale(scale);
	}

}
