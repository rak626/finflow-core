package com.rakesh.finflow.util.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BeanUtil implements ApplicationContextAware {
    private static ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        BeanUtil.ac = ac;
    }

    /**
     * Get spring bean
     *
     * @param beanName name of bean
     * @param clazz Type of the bean
     * @return a bean object
     * @param <T> bean type
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return ac.getBean(beanName, clazz);
    }

    /**
     * Get spring bean
     *
     * @param clazz Type of the bean
     * @return a bean object
     * @param <T> bean type
     */
    public static <T> T getBean(Class<T> clazz) {
        return ac.getBean(clazz);
    }
}