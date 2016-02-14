/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.rasptinker.model.mqtt;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletAmbientLightV2;
import com.tinkerforge.BrickletBarometer;
import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.BrickletMotionDetector;
import com.tinkerforge.BrickletPiezoSpeaker;
import com.tinkerforge.BrickletTilt;
import com.tinkerforge.IPConnection;
import static com.tinkerforge.IPConnectionBase.CONNECTION_STATE_CONNECTED;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rainer
 */
public class TinkerForgeDeviceHandler {

    private static final Logger LOGGER = Logger.getLogger(TinkerForgeDeviceHandler.class.getName());

    private IPConnection ipcon;
    private final List<ConnectionListener> connectionListener = new CopyOnWriteArrayList<>();

    public TinkerForgeDeviceHandler() {

        this.ipcon = new IPConnection();
        
        this.ipcon.addDisconnectedListener((short connectReason) -> {
            switch (connectReason) {
                case IPConnection.DISCONNECT_REASON_REQUEST:
                     LOGGER.info("DISCONNECT_REASON_REQUEST");
                    break;
                case IPConnection.DISCONNECT_REASON_ERROR:
                     LOGGER.info("DISCONNECT_REASON_ERROR");
                    break;
                case IPConnection.DISCONNECT_REASON_SHUTDOWN:
                     LOGGER.info("DISCONNECT_REASON_SHUTDOWN");
                    break;
                default:
                    LOGGER.info("????DISCONNECT_REASON UNKNOWN");
            }
            callConnectionListeners(ConnectionState.DISCONNECTED, null);
        });

        this.ipcon.addConnectedListener((short connectReason) -> {
            switch (connectReason) {
                case IPConnection.CONNECT_REASON_REQUEST:
                    break;
                case IPConnection.CONNECT_REASON_AUTO_RECONNECT:
                    break;
                default:
            }
            callConnectionListeners(ConnectionState.CONNECTED, null);

            try {
                TinkerForgeDeviceHandler.this.ipcon.enumerate();
            } catch (NotConnectedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

        });

        this.ipcon.addEnumerateListener((String uid, String connectedUid, char position,
                short[] hardwareVersion, short[] firmwareVersion,
                int deviceIdentifier, short enumerationType) -> {
                    LOGGER.info(uid);
                    LOGGER.info("UID: " + uid + ", Enumeration Type: " + enumerationType);
                    LOGGER.info("Position: " + position + ", Device Identifier: " + deviceIdentifier);
                    LOGGER.info("Hardware Version: " + getVersionString(hardwareVersion) + ", Firmware Version: " + getVersionString(firmwareVersion));

                    switch (enumerationType) {
                        case IPConnection.ENUMERATION_TYPE_AVAILABLE:
                            LOGGER.info("ENUMERATION_TYPE_AVAILABLE");
                            break;
                        case IPConnection.ENUMERATION_TYPE_CONNECTED:
                            LOGGER.info("ENUMERATION_TYPE_CONNECTED");
                            break;
                        case IPConnection.ENUMERATION_TYPE_DISCONNECTED:
                            LOGGER.info("ENUMERATION_TYPE_DISCONNECTED");
                            break;
                        default:
                    }

                });
    }

    private String getVersionString(short[] hardwareVersion) {
        String result = null;
        for (short nr : hardwareVersion) {
            if (result == null) {
                result = "" + nr;
            } else {
                result += "." + nr;
            }
        }

        return result;
    }

    public void addConnectionListener(ConnectionListener listener) {
        this.connectionListener.add(listener);
    }

    private void callConnectionListeners(ConnectionState state, Throwable connectionExp) {

        this.connectionListener.stream().forEach((listener) -> {
            listener.stateChanged(state, connectionExp);
        });
    }

    public void connect(final String host, final int port) {

        if (this.ipcon.getConnectionState() != CONNECTION_STATE_CONNECTED) {

            try{
                this.ipcon.connect(host, port);
                ipcon.enumerate();
                LOGGER.log(Level.INFO, "Tinkerforge connected");
            }catch (IOException | AlreadyConnectedException | NotConnectedException ex) {
               LOGGER.log(Level.WARNING, null, ex);
                callConnectionListeners(ConnectionState.ERROR, ex);
            }
        }
    }

    public void disconnect() {

        if (this.ipcon.getConnectionState() == CONNECTION_STATE_CONNECTED) {

            new Thread(() -> {
                try {
                    this.ipcon.disconnect();
                } catch (NotConnectedException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }).start();
        }
    }

    public BrickMaster addMaster(String masterUid) {

        return new BrickMaster(masterUid, ipcon);
    }

    public BrickletBarometer addBarometer(String uid, int callbackPeriode) {

        BrickletBarometer barometer = new BrickletBarometer(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    barometer.setAirPressureCallbackPeriod(callbackPeriode);
                    barometer.setAltitudeCallbackPeriod(callbackPeriode);
                } catch (TimeoutException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return barometer;
    }

    public BrickletAmbientLightV2  addAmbientLight(String uid, int callbackPeriode) {

        BrickletAmbientLightV2 ambient = new BrickletAmbientLightV2(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    ambient.setIlluminanceCallbackPeriod(callbackPeriode);
                } catch (TimeoutException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return ambient;
    }

    public BrickletHumidity addHumidity(String uid, int callbackPeriode) {

        BrickletHumidity ambient = new BrickletHumidity(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    ambient.setHumidityCallbackPeriod(callbackPeriode);
                } catch (TimeoutException | NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return ambient;
    }

    public BrickletTilt addTilt(String uid, int callbackPeriode) {

        BrickletTilt ambient = new BrickletTilt(uid, ipcon);

        return ambient;
    }

    public BrickletDualButton addDualButton(String uid, ButtonState left, ButtonState right) {

        BrickletDualButton dualButton = new BrickletDualButton(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    dualButton.setLEDState(left.getButtonState(), right.getButtonState());
                } catch (TimeoutException | NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return dualButton;
    }

    public BrickletLCD20x4 addLCD20x4(String uid, String line1) {

        BrickletLCD20x4 lcd = new BrickletLCD20x4(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    lcd.backlightOn();
                    lcd.clearDisplay();
                    lcd.writeLine((short) 0, (short) 0, line1);
                } catch (TimeoutException | NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return lcd;
    }

    public BrickletPiezoSpeaker addPiezoSpeaker(String uid) {

        BrickletPiezoSpeaker speaker = new BrickletPiezoSpeaker(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    speaker.beep(3000, 500);
                } catch (TimeoutException | NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return speaker;
    }

    public BrickletMotionDetector addMotionDetector(String uid) {

        BrickletMotionDetector detector = new BrickletMotionDetector(uid, ipcon);

        return detector;
    }

    public BrickletLEDStrip addLedStrip(String uid, LedChipType chipType) {

        short[] ledValue = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        BrickletLEDStrip led = new BrickletLEDStrip(uid, ipcon);
        addConnectionListener((ConnectionState state, Throwable exp) -> {
            if (state == ConnectionState.CONNECTED) {
                try {
                    led.setChipType(chipType.getType());
                    led.setFrameDuration(500);
                    led.setRGBValues(0, (short)1, ledValue,ledValue,ledValue);
                } catch (TimeoutException | NotConnectedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        });

        return led;
    }

    public enum ConnectionState {

        CONNECTED,
        DISCONNECTED,
        ERROR;
    }

    public enum LedChipType {

        WS2801(BrickletLEDStrip.CHIP_TYPE_WS2801),
        WS2811(BrickletLEDStrip.CHIP_TYPE_WS2811),
        WS2812(BrickletLEDStrip.CHIP_TYPE_WS2812);
        
        private final int type;

        private LedChipType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        
        
    }

    
    
    public enum ButtonState {

        LED_STATE_ON(BrickletDualButton.LED_STATE_ON),
        LED_STATE_OFF(BrickletDualButton.LED_STATE_OFF),
        LED_STATE_AUTO_TOGGLE_ON(BrickletDualButton.LED_STATE_AUTO_TOGGLE_ON),
        LED_STATE_AUTO_TOGGLE_OFF(BrickletDualButton.LED_STATE_AUTO_TOGGLE_OFF);

        private final short buttonState;

        ButtonState(short aButtonState) {
            this.buttonState = aButtonState;
        }

        public short getButtonState() {
            return buttonState;
        }
    }

    public interface ConnectionListener {

        public void stateChanged(ConnectionState state, Throwable connectionExp);
    }

}
