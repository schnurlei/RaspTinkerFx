/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinkerfx.model;

import com.tinkerforge.BrickletDualButton;

/**
 *
 * @author rainer
 */
public class DualButtonCounter
{

	public DualButtonCounter(TinkerForgeConnection connect, String uid)
	{
		BrickletDualButton button = connect.addDualButton(uid, TinkerForgeConnection.ButtonState.LED_STATE_ON, TinkerForgeConnection.ButtonState.LED_STATE_OFF);
		button.addStateChangedListener((short buttonL, short buttonR, short ledL, short ledR)->{
			if (buttonL == BrickletDualButton.BUTTON_STATE_PRESSED) {
                }
                if (buttonL == BrickletDualButton.BUTTON_STATE_RELEASED) {
                }
                if (buttonR == BrickletDualButton.BUTTON_STATE_PRESSED) {
                }
                if (buttonR == BrickletDualButton.BUTTON_STATE_RELEASED) {
                }
		});
	}
	
}
