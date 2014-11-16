package de.jdynameta.rasptinker.model.weather;

/**
 *
 * @author rainer
 */
	public enum Unit {
		Percent("%"),
		Lux("Lx"),
		MilliBar("mBar"),
		Metre("m"),
		DegreeCelsius("°C");
		
		private final String unitString;
		
		Unit(String anUnitString) {
			this.unitString = anUnitString;
		}

		public String getUnitString()
		{
			return unitString;
		}
	}
