package io.variability.jhipster.config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import infinispan.autoconfigure.embedded.InfinispanCacheConfigurer;
import infinispan.autoconfigure.embedded.InfinispanGlobalConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.jhipster.config.JHipsterProperties;
import java.util.concurrent.TimeUnit;
import org.infinispan.eviction.EvictionType;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.TransactionMode;
import infinispan.autoconfigure.embedded.InfinispanEmbeddedCacheManagerAutoConfiguration;
import org.infinispan.jcache.embedded.ConfigurationAdapter;
import org.infinispan.jcache.embedded.JCache;
import org.infinispan.jcache.embedded.JCacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
@Import(InfinispanEmbeddedCacheManagerAutoConfiguration.class)
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    // Initialize the cache in a non Spring-managed bean
    private static EmbeddedCacheManager cacheManager;

    public static EmbeddedCacheManager getCacheManager(){
        return cacheManager;
    }

    public static void setCacheManager(EmbeddedCacheManager cacheManager) {
        CacheConfiguration.cacheManager = cacheManager;
    }

    /**
     * Inject a {@link org.infinispan.configuration.global.GlobalConfiguration GlobalConfiguration} for Infinispan cache.
     * <p>
     * If the JHipster Registry is enabled, then the host list will be populated
     * from Eureka.
     *
     * <p>
     * If the JHipster Registry is not enabled, host discovery will be based on
     * the default transport settings defined in the 'config-file' packaged within
     * the Jar. The 'config-file' can be overridden using the application property
     * <i>jhipster.cache.inifnispan.config-file</i>
     *
     * <p>
     * If the JHipster Registry is not defined, you have the choice of 'config-file'
     * based on the underlying platform for hosts discovery. Infinispan
     * supports discovery natively for most of the platforms like Kubernets/OpenShift,
     * AWS, Azure and Google.
     *
     */
    @Bean
    public InfinispanGlobalConfigurer globalConfiguration(JHipsterProperties jHipsterProperties) {
        log.info("Defining Infinispan Global Configuration");
            return () -> GlobalConfigurationBuilder
                    .defaultClusteredBuilder().transport().defaultTransport()
                    .addProperty("configurationFile", jHipsterProperties.getCache().getInfinispan().getConfigFile())
                    .clusterName("infinispan-jhipster-cluster").globalJmxStatistics()
                    .enabled(jHipsterProperties.getCache().getInfinispan().isStatsEnabled())
                    .allowDuplicateDomains(true).build();
    }

    /**
     * Initialize cache configuration for Hibernate L2 cache and Spring Cache.
     * <p>
     * There are three different modes: local, distributed and replicated, and L2 cache options are pre-configured.
     *
     * <p>
     * It supports both jCache and Spring cache abstractions.
     * <p>
     * Usage:
     *  <ol>
     *      <li>
     *          jCache:
     *          <pre class="code">@CacheResult(cacheName="dist-app-data") </pre>
     *              - for creating a distributed cache. In a similar way other cache names and options can be used
     *      </li>
     *      <li>
     *          Spring Cache:
     *          <pre class="code">@Cacheable(value = "repl-app-data") </pre>
     *              - for creating a replicated cache. In a similar way other cache names and options can be used
     *      </li>
     *      <li>
     *          Cache manager can also be injected through DI/CDI and data can be manipulated using Infinispan APIs,
     *          <pre class="code">
     *          &#064;Autowired (or) &#064;Inject
     *          private EmbeddedCacheManager cacheManager;
     *
     *          void cacheSample(){
     *              cacheManager.getCache("dist-app-data").put("hi", "there");
     *          }
     *          </pre>
     *      </li>
     *  </ol>
     *
     */
    @Bean
    public InfinispanCacheConfigurer cacheConfigurer(JHipsterProperties jHipsterProperties) {
        log.info("Defining {} configuration", "app-data for local, replicated and distributed modes");
        final JHipsterProperties.Cache.Infinispan cacheInfo = jHipsterProperties.getCache().getInfinispan();

        return manager -> {
            // initialize application cache
            manager.defineConfiguration("local-app-data", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .eviction().type(EvictionType.COUNT).size(cacheInfo.getLocal().getMaxEntries()).expiration()
                .lifespan(cacheInfo.getLocal().getTimeToLiveSeconds(), TimeUnit.MINUTES).build());
            manager.defineConfiguration("dist-app-data", new ConfigurationBuilder()
                .clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(cacheInfo.getDistributed().getInstanceCount())
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled()).eviction()
                .type(EvictionType.COUNT).size(cacheInfo.getDistributed().getMaxEntries()).expiration().lifespan(cacheInfo.getDistributed()
                .getTimeToLiveSeconds(), TimeUnit.MINUTES).build());
            manager.defineConfiguration("repl-app-data", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .eviction().type(EvictionType.COUNT).size(cacheInfo.getReplicated()
                .getMaxEntries()).expiration().lifespan(cacheInfo.getReplicated().getTimeToLiveSeconds(), TimeUnit.MINUTES).build());

            // initilaize Hiberante L2 cache
            manager.defineConfiguration("entity", new ConfigurationBuilder().clustering().cacheMode(CacheMode.INVALIDATION_SYNC)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("replicated-entity", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_SYNC)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("local-query", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("replicated-query", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("timestamps", new ConfigurationBuilder().clustering().cacheMode(CacheMode.REPL_ASYNC)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .locking().concurrencyLevel(1000).lockAcquisitionTimeout(15000).build());
            manager.defineConfiguration("pending-puts", new ConfigurationBuilder().clustering().cacheMode(CacheMode.LOCAL)
                .jmxStatistics().enabled(cacheInfo.isStatsEnabled())
                .simpleCache(true).transaction().transactionMode(TransactionMode.NON_TRANSACTIONAL).expiration().maxIdle(60000).build());

            setCacheManager(manager);
        };
    }

    /**
     * <p>
     * Instance of {@link JCacheManager} with cache being managed by the underlying Infinispan layer. This helps to record stats
     * info if enabled and the same is accessible through MBX:javax.cache,type=CacheStatistics.
     *
     * <p>
     * jCache stats are at instance level. If you need stats at clustering level, then it needs to be retrieved from MBX:org.infinispan
     *
     */
    @Bean
    public JCacheManager getJCacheManager(EmbeddedCacheManager cacheManager, JHipsterProperties jHipsterProperties){
        return new InfinispanJCacheManager(Caching.getCachingProvider().getDefaultURI(), cacheManager,
            Caching.getCachingProvider(), jHipsterProperties);
    }

    class InfinispanJCacheManager extends JCacheManager {

        public InfinispanJCacheManager(URI uri, EmbeddedCacheManager cacheManager, CachingProvider provider,
                                       JHipsterProperties jHipsterProperties) {
            super(uri, cacheManager, provider);
            // register individual caches to make the stats info available.
            registerPredefinedCache("users", new JCache<Object, Object>(
                cacheManager.getCache("users").getAdvancedCache(), this,
                ConfigurationAdapter.create()));
            registerPredefinedCache(io.variability.jhipster.domain.User.class.getName(), new JCache<Object, Object>(
                cacheManager.getCache(io.variability.jhipster.domain.User.class.getName()).getAdvancedCache(), this,
                ConfigurationAdapter.create()));
            registerPredefinedCache(io.variability.jhipster.domain.Authority.class.getName(), new JCache<Object, Object>(
                cacheManager.getCache(io.variability.jhipster.domain.Authority.class.getName()).getAdvancedCache(), this,
                ConfigurationAdapter.create()));
            registerPredefinedCache(io.variability.jhipster.domain.User.class.getName() + ".authorities", new JCache<Object, Object>(
                cacheManager.getCache(io.variability.jhipster.domain.User.class.getName() + ".authorities").getAdvancedCache(), this,
                ConfigurationAdapter.create()));
            registerPredefinedCache(io.variability.jhipster.domain.SocialUserConnection.class.getName(), new JCache<Object, Object>(
                cacheManager.getCache(io.variability.jhipster.domain.SocialUserConnection.class.getName()).getAdvancedCache(), this,
                ConfigurationAdapter.create()));
            // jhipster-needle-infinispan-add-entry
            if (jHipsterProperties.getCache().getInfinispan().isStatsEnabled()) {
                for (String cacheName : cacheManager.getCacheNames()) {
                    enableStatistics(cacheName, true);
                }
            }
        }
    }

}
