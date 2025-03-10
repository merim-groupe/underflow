package com.merim.digitalpayment.underflow.i18n.sources;

import java.util.Properties;

/**
 * PropertiesSourceBuilder.
 *
 * @author Pierre Adam
 * @since 25.03.10
 */
public class PropertiesSourceBuilder extends AbstractI18nSourceBuilder<Properties, PropertiesSource, PropertiesSourceBuilder> {

    /**
     * Instantiates a new Properties source builder.
     */
    PropertiesSourceBuilder() {
        super(PropertiesSource::new);
    }
}
