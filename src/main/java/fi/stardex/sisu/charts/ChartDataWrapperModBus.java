package fi.stardex.sisu.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oleg on 14.07.16.
 */
public class ChartDataWrapperModBus {

    private List<Integer> data;

    public ChartDataWrapperModBus(int size){
        data = new ArrayList<>(size);
    }

    public ChartDataWrapperModBus(Integer[] data){
        this.data = new ArrayList<>(Arrays.asList(data));
    }

    public int getLenght(){
        return data.size();
    }

    public List<Integer> getAsList(){
        return data;
    }

    public Integer getByIndex(int index){
        return data.get(index);
    }

    public Integer setByIndex(int index, Integer value){
        return data.set(index,value);
    }

    public Integer[] getData(){
        return data.toArray(new Integer[data.size()]);
    }

    public void addData(Integer[] data){
        ArrayList<Integer> addData = new ArrayList<>(Arrays.asList(data));
        addData.toArray(data);
        this.data.addAll(addData);
    }

}
