package com.mtl.hulk.aop;

import com.mtl.hulk.AbstractHulk;
import com.mtl.hulk.HulkResourceManager;
import com.mtl.hulk.db.HulkDataSource;
import com.mtl.hulk.HulkInterceptor;
import com.mtl.hulk.extension.NetworkCommunication;
import com.mtl.hulk.bam.BusinessActivityManagerImpl;
import com.mtl.hulk.configuration.HulkProperties;
import com.mtl.hulk.snapshot.Snapshot;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public abstract class HulkAspectSupport extends AbstractHulk implements InitializingBean, BeanPostProcessor {

    public HulkAspectSupport(HulkProperties properties, ApplicationContext apc) {
        super(properties, apc);
    }

    public HulkAspectSupport(HulkProperties properties) {
        super(properties);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("bam")) {
            HulkResourceManager.setBam((BusinessActivityManagerImpl) bean);
        }
        if (beanName.equals("hulkDataSource")) {
            HulkResourceManager.setDatasource((HulkDataSource) bean);
        }
        if (beanName.equals("snapShot")) {
            HulkResourceManager.setSnapShot((Snapshot) bean);
        }
        if (beanName.equals("hulkTransactionInterceptor")
                    || beanName.equals("hulkBrokerInterceptor")) {
            HulkResourceManager.getInterceptors().add((HulkInterceptor) bean);
        }
        return bean;
    }

    @Override
    public void afterPropertiesSet() {
        if (applicationContext != null) {
            NetworkCommunication nc = applicationContext.getBean(NetworkCommunication.class);
            Map<String, Object> providers = nc.getProviders(applicationContext);
            for (Map.Entry provider : providers.entrySet()) {
                HulkResourceManager.getClients().put((String) provider.getKey(), provider.getValue());
            }
        }
    }

}
