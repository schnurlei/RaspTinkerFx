package de.jdynameta.rasptinker.model.weather;

import java.math.BigDecimal;

/**
 *
 * @author rainer
 */
public interface MeasureListener
{
	public void changed(Measurement aMeasurement, BigDecimal value);
}
