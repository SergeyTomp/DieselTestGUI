package fi.stardex.sisu.spring;

import fi.stardex.sisu.annotations.InitListeners;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class InitializeListenerBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Method[] methods = bean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            InitListeners annotation = method.getAnnotation(InitListeners.class);
            if (annotation != null)
                ReflectionUtils.invokeMethod(method, bean);
        }

        return bean;
    }
}
