package fi.stardex.sisu.model;

import fi.stardex.sisu.pdf.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class CodingReportModel {

    private ObservableMap<String, CodingResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());

    public ObservableMap<String, CodingResult> getResultObservableMap() {
        return resultObservableMap;
    }

    public void storeResult(List<String> codes) {

        IntStream.rangeClosed(0, codes.size() - 1).forEach(i -> resultObservableMap.put(Integer.toString(i + 1), new CodingResult(Integer.toString(i + 1), codes.get(i))));
    }

    public void clearResults(){

        resultObservableMap.clear();
    }

    public List<Result> getResultsList(){

        List<Result>results = new ArrayList<>(resultObservableMap.values());
        results.sort(Comparator.comparing(Result::getMainColumn));

        return results;
    }

    public class CodingResult implements Result {

        private String ledNumber;
        private String code;

        CodingResult(String ledNumber,String code) {

            this.ledNumber = ledNumber;
            this.code = code;
        }

        @Override
        public String getMainColumn() {
            return ledNumber;
        }

        @Override
        public String getSubColumn1() {
            return code;
        }

        @Override
        public String getSubColumn2() {
            return null;
        }

        @Override
        public List<String> getValueColumns() {
            return null;
        }
    }
}
