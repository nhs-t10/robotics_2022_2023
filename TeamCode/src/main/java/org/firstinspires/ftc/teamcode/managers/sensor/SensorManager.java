package org.firstinspires.ftc.teamcode.managers.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.auxilary.BasicMapEntry;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.Arrays;
import java.util.Map;

public class SensorManager extends FeatureManager {
    public Sensor[] sensors;
    public String[] sensorNames;

    /**
     * Make a SensorManager by only specifying the names. The constructor uses the given HardwareMap to resolve each name by itself.
     * <h3>Usage Example:</h3>
     * <pre><code>
     *      new SensorManager(
     *                 hardwareMap,
     *                 SensorManager.colorSensor("colorSensorA", "colorSensorB"),
     *                 SensorManager.touchSensor("touchSensorA", "touchSensorB"),
     *                 SensorManager.distanceSensor("distanceSensorA", "distanceSensorB")
     *         );
     * @see #colorSensor(String...)
     * @see #touchSensor(String...) 
     * @see #distanceSensor(String...)
     * @param _hardwareMap a hardware map that contains <u>all</u> of the listed hardware devices.
     * @param args A series of at least 3 map entries.
     */
    public SensorManager(HardwareMap _hardwareMap, Map.Entry<String, String[]>... args) {
        String[] _colorSensor = new String[0], _touchSensor = new String[0], _distanceSensor = new String[0];
        for (Map.Entry<String, String[]> a : args) {
            if (a.getKey().equals("crservo")) _colorSensor = a.getValue();
            else if (a.getKey().equals("servo")) _touchSensor = a.getValue();
            else if (a.getKey().equals("motor")) _distanceSensor = a.getValue();
        }
        sensors = new Sensor[_colorSensor.length + _touchSensor.length + _distanceSensor.length];
        sensorNames = PaulMath.concatArrays(_colorSensor, _touchSensor, _distanceSensor);

        for(int i = 0; i < sensorNames.length; i++) {
            if(i < _colorSensor.length) {
                sensors[i] = new ColorSensor(_hardwareMap.get(NormalizedColorSensor.class, sensorNames[i]));
            } else if(i < _colorSensor.length + _touchSensor.length) {
                sensors[i] = new TouchSensor(_hardwareMap.get(com.qualcomm.robotcore.hardware.TouchSensor.class, sensorNames[i]));
            } else {
                sensors[i] = new DistanceSensor(_hardwareMap.get(com.qualcomm.robotcore.hardware.DistanceSensor.class, sensorNames[i]));
            }
        }
    }

    /**
     * A helper method, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     * @param names ColorSensor names
     * @return A map entry, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     */
    public static BasicMapEntry<String, String[]> colorSensor(String... names) {
        return new BasicMapEntry<String, String[]>("colorSensor", names);
    }
    /**
     * A helper method, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     * @param names TouchSensor names
     * @return A map entry, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     */
    public static BasicMapEntry<String, String[]> touchSensor(String... names) {
        return new BasicMapEntry<String, String[]>("touchSensor", names);
    }
    /**
     * A helper method, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     * @param names DistanceSensor names
     * @return A map entry, for use in {@link #SensorManager(HardwareMap, Map.Entry[])}
     */
    public static BasicMapEntry<String, String[]> distanceSensor(String... names) {
        return new BasicMapEntry<String, String[]>("distanceSensor", names);
    }

    /**
     * Update all sensors. The "update" operation isn't necessarily meaningful for every sensor.
     */
    public void updateAllSensors() {
        for(Sensor s : sensors) {
            s.update();
        }
    }

    /**
     * Update a given sensor. The "update" operation isn't necessarily meaningful for every sensor.
     * @param index
     */
    public void updateSensor(int index) {
        this.sensors[index].update();
    }

    /**
     * Get the index of the given sensor's name in the internal sensor array
     * @param name The sensor to look up
     * @return THe internal name.
     */
    public int indexOf(String name) {
        for(int i = 0; i < sensorNames.length; i++) {
            if(sensorNames[i].equals(name)) return i;
        }
        throw new IllegalArgumentException("Sensor `" + name + "` is not registered in the SensorManager!");
    }

    /**
     * For color sensors, return the Android color integer. For non-color sensors, return some other number.
     * @param name the name of the sensor to get
     * @return the color integer, if it's a sensor; otherwise, some undefined number
     */
    public int getColorInteger(String name) {
        return (int) sensors[indexOf(name)].getNumberValue();
    }

    /**
     * Check whether a sensor is currently special.
     * The "special check" is different for each sensor type-- see the sensor class for more info.
     * @param name The name of the sensor to check
     * @return Whether the given sensor is special.
     */
    public boolean isSpecial(String name) {
        return sensors[indexOf(name)].isSpecial();
    }


}
