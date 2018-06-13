package fi.stardex.sisu.util.wrappers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class BeanList<E> implements Iterable<E> {

    private List<E> beans;

    private Comparator<E> comparator;

    public BeanList(E e) {
        beans = Collections.singletonList(e);
    }

    public BeanList(List<E> beans) {
        this.beans = beans;
    }

    public BeanList(List<E> beans, Comparator<E> comparator) {
        this.beans = beans;
        this.comparator = comparator;
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        beans.forEach(action);
    }

    @Override
    public Iterator<E> iterator() {
        return beans.iterator();
    }

    public E get(int index) {
        return beans.get(index);
    }

    public E max() {
        return Collections.max(beans, comparator);
    }

}