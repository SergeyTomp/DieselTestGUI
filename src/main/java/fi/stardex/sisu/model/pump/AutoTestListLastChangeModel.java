package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AutoTestListLastChangeModel {

    private ObjectProperty<PumpTestWrapper.ChangedParameters> changedParameters = new SimpleObjectProperty<>();

    public ObjectProperty<PumpTestWrapper.ChangedParameters> changedParametersProperty() {
        return changedParameters;
    }

    public class PumpTestWrapper implements ChangeListener<Boolean> {

        private PumpTest pumpTest;
        private BooleanProperty isIncluded = new SimpleBooleanProperty(true);
        private int listViewIndex;

        public PumpTest getPumpTest() {
            return pumpTest;
        }
        public BooleanProperty isIncludedProperty() {
            return isIncluded;
        }

        public void setListViewIndex(int index) {
            this.listViewIndex = index;
        }

        public PumpTestWrapper(PumpTest pumpTest, int index) {
            this.pumpTest = pumpTest;
            listViewIndex = index;
            isIncluded.addListener(this);
        }

        @Override
        public String toString() {
            return pumpTest.toString();
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue != null) {

                changedParameters.setValue(new ChangedParameters(newValue, listViewIndex));
            }
        }

        public class ChangedParameters {
            private boolean include;
            private int lastChangeIndex;

            public boolean isInclude() {
                return include;
            }
            public int getLastChangeIndex() {
                return lastChangeIndex;
            }

            public void setInclude(boolean include) {
                this.include = include;
            }

            public void setLastChangeIndex(int lastChangeIndex) {
                this.lastChangeIndex = lastChangeIndex;
            }

            public ChangedParameters(boolean include, int lastChangeIndex) {
                this.include = include;
                this.lastChangeIndex = lastChangeIndex;
            }
        }
    }
}
