package org.demo.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware
{

    private static ApplicationContext applicationContext;

    @SuppressWarnings("java:S2696")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        BeanUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz)
    {
        return applicationContext.getBean(clazz);
    }
}
