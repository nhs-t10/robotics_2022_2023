package org.firstinspires.ftc.teamcode.unitTests.dummy;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opmode.TelemetryImpl;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import androidx.annotation.Nullable;

public class DummyTelemetry implements Telemetry {

    @Override
    public Item addData(String caption, String format, Object... args) {
        //FeatureManager.logger.log(caption + "\t" + String.format(format, args));
        return new DummyItem();
    }

    @Override
    public Item addData(String caption, Object value) {
        //FeatureManager.logger.log(caption + "\t" + value);
        return new DummyItem();
    }

    @Override
    public <T> Item addData(String caption, Func<T> valueProducer) {
        //FeatureManager.logger.log(caption + "\t" + valueProducer.value());
        return new DummyItem();
    }

    @Override
    public <T> Item addData(String caption, String format, Func<T> valueProducer) {
        //FeatureManager.logger.log(caption + "\t" + String.format(format, valueProducer.value()));
        return new DummyItem();
    }

    @Override
    public boolean removeItem(Item item) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void clearAll() {

    }

    @Override
    public Object addAction(Runnable action) {
        return null;
    }

    @Override
    public boolean removeAction(Object token) {
        return false;
    }

    @Override
    public void speak(String text) {

    }

    @Override
    public void speak(String text, String languageCode, String countryCode) {

    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public Line addLine() {
        return null;
    }

    @Override
    public Line addLine(String lineCaption) {
        return null;
    }

    @Override
    public boolean removeLine(Line line) {
        return false;
    }

    @Override
    public boolean isAutoClear() {
        return false;
    }

    @Override
    public void setAutoClear(boolean autoClear) {

    }

    @Override
    public int getMsTransmissionInterval() {
        return 0;
    }

    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {

    }

    @Override
    public String getItemSeparator() {
        return null;
    }

    @Override
    public void setItemSeparator(String itemSeparator) {

    }

    @Override
    public String getCaptionValueSeparator() {
        return null;
    }

    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {

    }

    @Override
    public void setDisplayFormat(DisplayFormat displayFormat) {

    }

    private DummyLog log = new DummyLog();
    @Override
    public DummyLog log() {
        return log;
    }
    public static class DummyLog implements Telemetry.Log {

        public String logText;

        private DummyLog() {
            this.logText = "";
        }

        public String getLogText() {
            return logText;
        }

        @Override
        public int getCapacity() {
            return 0;
        }

        @Override
        public void setCapacity(int capacity) {

        }

        @Override
        public DisplayOrder getDisplayOrder() {
            return DisplayOrder.OLDEST_FIRST;
        }

        @Override
        public void setDisplayOrder(DisplayOrder displayOrder) {
        }

        @Override
        public void add(String entry) {
            logText += entry + "\n";
            FeatureManager.logger.fallbackBackend.println(entry);
        }

        @Override
        public void add(String format, Object... args) {
            add(String.format(format, args));
        }

        @Override
        public void clear() {

        }
    }
    public static class DummyItem implements Item {

        private boolean retained;
        private String caption;
        private String value;

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public Item setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        @Override
        public Item setValue(String format, Object... args) {
            return this;
        }

        @Override
        public Item setValue(Object value) {
            return this;
        }

        @Override
        public <T> Item setValue(Func<T> valueProducer) {
            return this;
        }

        @Override
        public <T> Item setValue(String format, Func<T> valueProducer) {
            return this;
        }

        @Override
        public Item setRetained(@Nullable Boolean retained) {
            this.retained = retained == null || retained;
            return this;
        }

        @Override
        public boolean isRetained() {
            return retained;
        }

        @Override
        public Item addData(String caption, String format, Object... args) {
            return this;
        }

        @Override
        public Item addData(String caption, Object value) {
            return this;
        }

        @Override
        public <T> Item addData(String caption, Func<T> valueProducer) {
            return this;
        }

        @Override
        public <T> Item addData(String caption, String format, Func<T> valueProducer) {
            return this;
        }
    }
}
