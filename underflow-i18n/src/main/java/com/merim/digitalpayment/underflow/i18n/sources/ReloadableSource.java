package com.merim.digitalpayment.underflow.i18n.sources;

import com.merim.digitalpayment.underflow.i18n.I18nSource;
import lombok.NonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * ReloadableSource.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public class ReloadableSource implements I18nSource {

    /**
     * The Source supplier.
     */
    private final Supplier<I18nSource> sourceSupplier;

    /**
     * The Last reload.
     */
    private final long reloadMinimalInterval;

    /**
     * The Last reload.
     */
    private final long lastReload;

    /**
     * The Source.
     */
    private I18nSource source;

    /**
     * Instantiates a new Reloadable source.
     *
     * @param sourceSupplier        the source supplier
     * @param reloadMinimalInterval the reload minimal interval
     */
    protected ReloadableSource(final Supplier<I18nSource> sourceSupplier, final Long reloadMinimalInterval) {
        this.sourceSupplier = sourceSupplier;
        this.reloadMinimalInterval = reloadMinimalInterval == null ? 3000 : reloadMinimalInterval;
        this.lastReload = System.currentTimeMillis();
        this.source = sourceSupplier.get();
    }

    /**
     * Wrap reloadable source.
     *
     * @param sourceSupplier the source supplier
     * @return the reloadable source
     */
    public static I18nSource wrap(@NonNull final Supplier<I18nSource> sourceSupplier) {
        return ReloadableSource.wrap(sourceSupplier, null);
    }

    /**
     * Wrap 18 n source.
     *
     * @param sourceSupplier        the source supplier
     * @param reloadMinimalInterval the reload minimal interval
     * @return the 18 n source
     */
    public static I18nSource wrap(@NonNull final Supplier<I18nSource> sourceSupplier, final Long reloadMinimalInterval) {
        return new ReloadableSource(sourceSupplier, reloadMinimalInterval);
    }

    /**
     * With source r.
     *
     * @param <R>   the type parameter
     * @param logic the logic
     * @return the r
     */
    private <R> R withSource(final Function<I18nSource, R> logic) {
        synchronized (this) {
            if (this.hasExpired()) {
                this.source = this.sourceSupplier.get();
            }
            return logic.apply(this.source);
        }
    }

    /**
     * Has expired boolean.
     *
     * @return the boolean
     */
    public boolean hasExpired() {
        return System.currentTimeMillis() - this.lastReload > this.reloadMinimalInterval;
    }

    @Override
    public Collection<Locale> getAvailableLocales() {
        return this.withSource(I18nSource::getAvailableLocales);
    }

    @Override
    public boolean hasLocale(final Locale locale) {
        return this.withSource(source -> source.hasLocale(locale));
    }

    @Override
    public boolean hasMessage(final String key) {
        return this.withSource(source -> source.hasMessage(key));
    }

    @Override
    public boolean hasMessage(final Locale locale, final String key) {
        return this.withSource(source -> source.hasMessage(locale, key));
    }

    @Override
    public Optional<String> getMessage(final Locale locale, final String key) {
        return this.withSource(source -> source.getMessage(locale, key));
    }
}
