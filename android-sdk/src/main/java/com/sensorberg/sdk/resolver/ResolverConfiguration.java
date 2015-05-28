package com.sensorberg.sdk.resolver;


import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;

/**
 * Class {@link ResolverConfiguration} provides configuration functionality for the {@link Resolver}.
 */
public final class ResolverConfiguration implements Serializable {

    static final long serialVersionUID = 3L;

    public String apiToken;

    private URL resolverLayoutURL;

    private JSONObject payload;

    /**
     * Sets the API token of the {@link ResolverConfiguration}.
     *
     * @param apiToken the API token to be set
     */
    public boolean setApiToken(String apiToken) {
        boolean changed = this.apiToken != null && !this.apiToken.equals(apiToken);
        this.apiToken = apiToken;
        return changed;
    }

    public URL getResolverLayoutURL() {
        return resolverLayoutURL;
    }

    public void setResolverLayoutURL(URL resolverLayoutURL) {
        this.resolverLayoutURL = resolverLayoutURL;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public boolean setPayload(JSONObject payload) {
        boolean changed = this.payload != null && !this.payload.equals(payload);
        this.payload = payload;
        return changed;
    }
}
