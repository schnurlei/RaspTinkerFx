/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.model;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickServo;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.BrickletNFCRFID;
import com.tinkerforge.BrickletPiezoSpeaker;
import com.tinkerforge.BrickletTilt;

/**
 *
 * @author rainer
 */
public enum TinkerForgeDeviceTypes
{
	BRICKDC(BrickDC.DEVICE_IDENTIFIER,"Brick DC"),
    BRICK_MASTER(    BrickMaster.DEVICE_IDENTIFIER, "Brick Master"),
    Brick_Servo(    BrickServo.DEVICE_IDENTIFIER, "Brick Servo"),
    Brick_Stepper(    15, "Brick Stepper"),
    Brick_IMU(    16, "Brick IMU"),
    Brick_RED(   17,"Brick RED"),
    Bricklet_Ambient_Light(    BrickletAmbientLight.DEVICE_IDENTIFIER,"Bricklet Ambient Light"),
//    BRICKDC(    23,"Bricklet Current12"),
//    BRICKDC(    24,"Bricklet Current25"),
//    BRICKDC(    25, "Bricklet Distance IR"),
//    BRICKDC(    26, "Bricklet Dual Relay"),
      BRICKLET_HUMIDITY(    BrickletHumidity.DEVICE_IDENTIFIER, "Bricklet Humidity"),
//    BRICKDC(    28, "Bricklet IO-16"),
//    BRICKDC(    29, "Bricklet IO-4"),
//    BRICKDC(    210, 	"Bricklet Joystick"),
//    BRICKDC(    211, 	"Bricklet LCD 16x2"),
      BRICKLETLCD20X4(    BrickletLCD20x4.DEVICE_IDENTIFIER, 	"Bricklet LCD 20x4"),
//    BRICKDC(    213, 	"Bricklet Linear Poti"),
//    BRICKDC(    214, 	"Bricklet Piezo Buzzer"),
//    BRICKDC(    215, 	"Bricklet Rotary Poti"),
//    BRICKDC(    216, 	"Bricklet Temperature"),
//    BRICKDC(    217, 	"Bricklet Temperature IR"),
//    BRICKDC(    218,	"Bricklet Voltage"),
//    BRICKDC(    219,	"Bricklet Analog In"),
//    BRICKDC(    220, 	"Bricklet Analog Out"),
      BRICKLETBAROMETER(    BrickletBarometer.DEVICE_IDENTIFIER, 	"Bricklet Barometer"),
//    BRICKDC(    222, 	"Bricklet GPS"),
//    BRICKDC(    223, 	"Bricklet Industrial Digital In 4"),
//    BRICKDC(    224, 	"Bricklet Industrial Digital Out 4"),
//    BRICKDC(    225, 	"Bricklet Industrial Quad Relay"),
//    BRICKDC(    226, 	"Bricklet PTC"),
//    BRICKDC(    227, 	"Bricklet Voltage/Current"),
//    BRICKDC(    228, 	"Bricklet Industrial Dual 0-20mA"),
//    BRICKDC(    229, 	"Bricklet Distance US"),
      BRICKLET_DUAL_BUTTON(BrickletDualButton.DEVICE_IDENTIFIER, "Bricklet Dual Button"),
      BRICKLET_LED(    BrickletLEDStrip.DEVICE_IDENTIFIER, "Bricklet LED Strip"),
//    BRICKDC(    232, 	"Bricklet Moisture"),
//    BRICKDC(    233, 	"Bricklet Motion Detector"),
//    BRICKDC(    234, 	"Bricklet Multi Touch"),
//    BRICKDC(    235, 	"Bricklet Remote Switch"),
//    BRICKDC(    236, 	"Bricklet Rotary Encoder"),
//    BRICKDC(    237, 	"Bricklet Segment Display 4x7"),
//    BRICKDC(    238, 	"Bricklet Sound Intensity"),
	  BRICKLET_TILT(    BrickletTilt.DEVICE_IDENTIFIER, 	"Bricklet Tilt"),
//    BRICKDC(    240, 	"Bricklet Hall Effect"),
//    BRICKDC(    241, 	"Bricklet Line"),
      BRICKLET_PIEZO_SPEAKER(BrickletPiezoSpeaker.DEVICE_IDENTIFIER, 	"Bricklet Piezo Speaker"),
//    BRICKDC(    243, 	"Bricklet Color"),
//    BRICKDC(    244, 	"Bricklet Solid State Relay"),
//    BRICKDC(    245, 	"Bricklet Heart Rate"),
    Bricklet_NFC_RFID(    BrickletNFCRFID.DEVICE_IDENTIFIER, 	"Bricklet NFC/RFID");	
	
	
	private final int deviceIdentifier;
	private final String deviceName;
	
	TinkerForgeDeviceTypes(int aDeviceIdentifier, String aDeviceName) {
		this.deviceName = aDeviceName;
		this.deviceIdentifier = aDeviceIdentifier;
	}

	public int getDeviceIdentifier()
	{
		return deviceIdentifier;
	}

	public String getDeviceName()
	{
		return deviceName;
	}
}
