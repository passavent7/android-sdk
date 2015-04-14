package com.sensorberg.sdk;

import com.sensorberg.sdk.resolver.ResolverConfiguration;

import java.io.Serializable;

public class ServiceConfiguration implements Serializable{

    private static final long serialVersionUID = 3L;

    public ResolverConfiguration resolverConfiguration;

    public ServiceConfiguration( ResolverConfiguration resolverConfiguration) {
        this.resolverConfiguration = resolverConfiguration;
    }

    public boolean isComplete() {
        return resolverConfiguration  != null &&
                resolverConfiguration.apiToken != null;
    }
}
