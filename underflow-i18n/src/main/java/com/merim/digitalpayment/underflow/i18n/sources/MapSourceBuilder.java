package com.merim.digitalpayment.underflow.i18n.sources;

import java.util.Map;

/**
 * MapSourceBuilder.
 *
 * @author Pierre Adam
 * @since 25.03.10
 */
public class MapSourceBuilder extends AbstractI18nSourceBuilder<Map<String, String>, MapSource, MapSourceBuilder> {

    /**
     * Instantiates a new Map source builder.
     */
    public MapSourceBuilder() {
        super(MapSource::new);
    }
}
