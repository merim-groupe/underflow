package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.io.InputStream;

/**
 * AssetsRepresentation.
 *
 * @author Pierre Adam
 * @since 23.07.05
 */
public interface AssetRepresentation {

    String getEtag();

    InputStream open();
}
