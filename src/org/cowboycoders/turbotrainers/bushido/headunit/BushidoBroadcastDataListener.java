package org.cowboycoders.turbotrainers.bushido.headunit;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.utils.ArrayUtils;

import static org.cowboycoders.ant.utils.ArrayUtils.*;

public class BushidoBroadcastDataListener implements BroadcastListener<BroadcastDataMessage> {
    Byte[] data;
    double speed;
    double power;
    double cadence;
    double distance;
    double heartRate;
    // Packet identifiers
    Byte[] bushidoPaused = {(byte) 0xAD , 0x01 , 0x03 , 0x0a , 0x00 , 0x00, 0x0a, 0x02};
    Byte[] bushidoLogging = {(byte) 0xAD, 0x01, 0x02};
    Byte[] bushidoDataIdentifier = {(byte) 0xDD};
    Byte[] bushidoSPCidentifier = {(byte) 0xDD,(byte) 0x01};
    Byte[] bushidoDHidentifier = {(byte) 0xDD,(byte) 0x02};
    Byte[] bushidoStatusIdentifier = {(byte) 0xAD};
    private BushidoInternalListener bushidoListener;
    
    public BushidoBroadcastDataListener(BushidoInternalListener bushidoListener) {
      this.bushidoListener = bushidoListener;
    }
    
    @Override
    public void receiveMessage(BroadcastDataMessage message) {
        data = message.getData();
        int [] unsignedData = ArrayUtils.unsignedBytesToInts(data);
        if (arrayStartsWith(bushidoDataIdentifier, data)) {
            data = message.getData();
            if (arrayStartsWith(bushidoSPCidentifier, data)) {
                // speed in km/h
                speed = ((unsignedData [2] << 8) + unsignedData [3]) / 10;
                power = (unsignedData [4] << 8) + unsignedData [5];
                cadence = unsignedData [6];
                bushidoListener.onSpeedChange(speed);
                bushidoListener.onPowerChange(power);
                bushidoListener.onCadenceChange(cadence);
                //System.out.println("Speed: " + speed);
                //System.out.println("Power: " + power);
                //System.out.println("Cadence: " + cadence);
            }
            if (arrayStartsWith(bushidoDHidentifier, data)) {
                // 3 byte (24 bit) shift will wrap an int
                distance = ((long)unsignedData [2] << 24) + (unsignedData [3] << 16) + (unsignedData [4] << 8) + unsignedData [5];
                heartRate = unsignedData [6];
                bushidoListener.onDistanceChange(distance);
                //System.out.println("Distance: " + distance);
                bushidoListener.onHeartRateChange(heartRate);
               // System.out.println("Heart rate: " + heartRate);
            }
        } else if (arrayStartsWith(bushidoStatusIdentifier, data)){
            if (arrayStartsWith(bushidoPaused, data)){
                //Send unpause command
                bushidoListener.onRequestPauseStatus();
            } else if (arrayStartsWith(bushidoLogging, data)){
                bushidoListener.onRequestData();
                //Send data
            }
        }
    }
    

}