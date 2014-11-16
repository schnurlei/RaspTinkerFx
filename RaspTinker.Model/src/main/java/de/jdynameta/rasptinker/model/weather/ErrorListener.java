
package de.jdynameta.rasptinker.model.weather;

/**
 *
 * @author rainer
 */
public interface ErrorListener
{
	public void error(Measurement aMeasurement, Exception ex);
}
