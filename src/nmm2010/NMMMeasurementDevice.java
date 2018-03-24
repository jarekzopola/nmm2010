/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nmm2010;

import java.io.Serializable;

/**
 *
 * @author Jarek
 */
public class NMMMeasurementDevice implements Serializable {

    String deviceName="device name empty";
    String deviceCalibration="evice calibration empty";

    public String getDevceName() {
        return this.deviceName;
    }

    public void setDeviceName(String _deviceName) {
        this.deviceName=_deviceName;
    }

    public String getDeviceCalibration() {
        return this.deviceCalibration;
    }

    public void setDeviceCalibration(String _deviceCalibration) {
        this.deviceCalibration=_deviceCalibration;
    }
}
