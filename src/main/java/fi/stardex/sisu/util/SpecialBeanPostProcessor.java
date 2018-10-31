package fi.stardex.sisu.util;

import fi.stardex.sisu.annotations.InvokeAfterInit;
import fi.stardex.sisu.annotations.PostProcess;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpecialBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class type = bean.getClass();
        if(type.isAnnotationPresent(PostProcess.class)){
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if(m.getAnnotation(InvokeAfterInit.class) != null){
                    try {
                        m.setAccessible(true);
                        m.invoke(bean);
                        m.setAccessible(false);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bean;
    }
}
