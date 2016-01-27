/*
 * Copyright 2014 rainer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.rasptinker.model.led;

import com.tinkerforge.BrickletLEDStrip;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import de.jdynameta.rasptinker.model.TinkerForgeConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rainer
 */
public class LedMatrix64 {

    private final TinkerForgeConnection connection;

        private static final short[] pattern0
            = new short[64];

    private static final short[] patternXX
            = { 1, 0, 0, 4, 1, 0, 0, 4,
                0, 1, 0, 1, 4, 0, 4, 0,
                0, 0, 1, 0, 0, 4, 0, 0,
                0, 1, 0, 1, 4, 0, 4, 0,
                1, 0, 0, 4, 1, 0, 0, 4,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                2, 0, 0, 0, 3, 0, 0, 1};

    private final Map<Short, RgbColor> index2ColorMap;
    private int pattern0idx = 0;
    private int pattern0color = 0;

    public LedMatrix64(String ledUid) {

        this.index2ColorMap = new HashMap<>();
        this.index2ColorMap.put((short) 0, new RgbColor(0, 0, 0));
        this.index2ColorMap.put((short) 1, new RgbColor(0, 0, 255));
        this.index2ColorMap.put((short) 2, new RgbColor(255, 0, 0));
        this.index2ColorMap.put((short) 3, new RgbColor(120, 120, 120));
        this.index2ColorMap.put((short) 4, new RgbColor(60, 60, 255));

        this.connection = new TinkerForgeConnection();
        BrickletLEDStrip ledStrip = this.connection.addLedStrip(ledUid, TinkerForgeConnection.LedChipType.WS2812);

        final int NUM_LEDS = 16;
        short[] red = new short[NUM_LEDS];
        short[] green = new short[NUM_LEDS];
        short[] blue = new short[NUM_LEDS];

        ledStrip.addFrameRenderedListener((int length) -> {

            if (pattern0idx == 0) {
                pattern0color = (pattern0color+10) %255;
            }
            this.index2ColorMap.put((short) pattern0color, new RgbColor(pattern0color, pattern0color, pattern0color));

            pattern0[pattern0idx++] = (short)pattern0color;
            pattern0idx %= 64;

            // Set new data for next render cycle
            try {
                for (int i = 0; i < patternXX.length; i += NUM_LEDS) {
                    for (int j = 0; j < NUM_LEDS; j++) {
                        RgbColor color = index2ColorMap.get(patternXX[i+j]);
                        red[j] = color.red;
                        green[j] = color.green;
                        blue[j] = color.blue;
                    }
                    ledStrip.setRGBValues(i, (short) NUM_LEDS, blue, red, green);
                }
            } catch (TimeoutException | NotConnectedException e) {
                System.out.println(e);
            }

        });
        connection.connect("localhost", 4223);

    }

    public static void main(String[] args) {
        new LedMatrix64("p4R");
        System.out.println("Press key to exit");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(LedMatrix64.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class RgbColor {

        private short red, green, blue;

        public RgbColor(int red, int green, int blue) {
            this.red = (short) red;
            this.green = (short) green;
            this.blue = (short) blue;
        }

    }
}
