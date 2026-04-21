package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.handlers.flows.FlowAssetsHandler;
import com.merim.digitalpayment.underflow.handlers.flows.assets.ResourceAssetLoader;
import jakarta.ws.rs.Path;

/**
 * SampleAssetHandler.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
@Path("/assets")
public class SampleAssetHandler extends FlowAssetsHandler {

    /**
     * Instantiates a new Sample asset handler.
     */
    public SampleAssetHandler() {
        super(new ResourceAssetLoader(MainSample.class, "/assets"));
    }
}
