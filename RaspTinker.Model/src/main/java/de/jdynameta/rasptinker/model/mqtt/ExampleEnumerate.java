/*
 * Copyright 2016 rainer.
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
package de.jdynameta.rasptinker.model.mqtt;

import com.tinkerforge.IPConnection;

/**
 *
 * @author rainer
 */
public class ExampleEnumerate {
   
      private static final String HOST = "localhost";
    private static final int PORT = 4223;

    // Note: To make the example code cleaner we do not handle exceptions. Exceptions you
    //       might normally want to catch are described in the documentation
    public static void main(String args[]) throws Exception {
        // Create connection and connect to brickd
        IPConnection ipcon = new IPConnection();
        ipcon.connect(HOST, PORT);

        // Register enumerate listener and print incoming information
        ipcon.addEnumerateListener(new IPConnection.EnumerateListener() {
            public void enumerate(String uid, String connectedUid, char position,
                                  short[] hardwareVersion, short[] firmwareVersion,
                                  int deviceIdentifier, short enumerationType) {
                System.out.println("UID:               " + uid);
                System.out.println("Enumeration Type:  " + enumerationType);

                if(enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
                    System.out.println("");
                    return;
                }

                System.out.println("Connected UID:     " + connectedUid);
                System.out.println("Position:          " + position);
                System.out.println("Hardware Version:  " + hardwareVersion[0] + "." +
                                                           hardwareVersion[1] + "." +
                                                           hardwareVersion[2]);
                System.out.println("Firmware Version:  " + firmwareVersion[0] + "." +
                                                           firmwareVersion[1] + "." +
                                                           firmwareVersion[2]);
                System.out.println("Device Identifier: " + deviceIdentifier);
                System.out.println("");
            }
        });

        ipcon.enumerate();

        System.out.println("Press key to exit"); System.in.read();

        ipcon.disconnect();
    }
}
