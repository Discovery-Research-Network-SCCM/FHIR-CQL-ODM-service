package org.partners.usciitg_prep.fhir;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.partners.usciitg_prep.fhir.services.CrfElmQueryService;
import org.partners.usciitg_prep.fhir.services.DagFHIRServerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.uhn.fhir.jpa.dao.DaoConfig;


@Configuration
@EnableTransactionManagement()
@ComponentScan(basePackages={"org.partners.usciitg_prep.terminology.jpa","org.partners.usciitg_prep.fhir"},
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern="org.partners.usciitg_prep.fhir.*"))
@PropertySource("classpath:/org/partners/usciitg_prep/terminology/jpa/jpaApplicationTest.properties")
public class FhirServiceTestConfig  {	
	@Value("${jndi.datasource}")
	private String jndiDatasource;
	
	@Value("${spring.jpa.orm}")
	private String orm;
	@Value("${spring.jpa.persistenceUnitName}")
	private String persistenceUnitName;
	@Value("${spring.jpa.defaultPersistenceUnitName}")
	private String defaultPersistenceUnitName;
	
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	@Value("${hibernate.format_sql}")
	private String hibernateFormatSql;
	@Value("${hibernate.show_sql}")
	private String hibernateShowSql;
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddlAuto;
	@Value("${hibernate.jdbc.batch_size}")
	private String hibernateJdbcBatchSize;
	@Value("${hibernate.cache.use_query_cache}")
	private String hibernateCacheUseQueryCache;
	@Value("${hibernate.cache.use_second_level_cache}")
	private String hibernateCacheUseSecondLevelCache;
	@Value("${hibernate.cache.use_structured_entries}")
	private String hibernateCacheUseStructuredEntries;
	@Value("${hibernate.cache.use_minimal_puts}")
	private String hibernateCacheUseMinimalPuts;
	@Value("${hibernate.search.default.directory_provider}")
	private String hibernateSearchDefaultDirectoryProvider;
	@Value("${hibernate.search.default.indexBase}")
	private String hibernateSearchDefaultIndexBase;
	@Value("${hibernate.search.lucene_version}")
	private String hibernateSearchLuceneVersion;
	
	@Bean
    public FhirService fhirService() {
        return new FhirService();
    } 
	
	@Bean
    public DagFHIRServerService dagFHIRServerService() {
        return new DagFHIRServerService();
    }
	
	@Bean
    public CrfElmQueryService crfElmQueryService() {
        return new CrfElmQueryService();
    }
	
	/**
	 * Configure FHIR properties around the the JPA server via this bean
	 */
	@Bean
	public DaoConfig daoConfig() {
		DaoConfig retVal = new DaoConfig();
		retVal.setSubscriptionEnabled(true);
		retVal.setSubscriptionPollDelay(5000);
		retVal.setSubscriptionPurgeInactiveAfterMillis(DateUtils.MILLIS_PER_HOUR);
		retVal.setAllowMultipleDelete(true);
		return retVal;	
	}
	
	@Bean
    public DataSource dataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        DataSource dataSource = dsLookup.getDataSource(jndiDatasource);
        return dataSource;
    } 
	
/*    @Bean
    public PersistenceUnitManager persistenceUnitManager() {
    	DefaultPersistenceUnitManager pum = new DefaultPersistenceUnitManager();
    	pum.setPackagesToScan("org.partners.usciitg_prep.terminology.jpa","org.partners.usciitg_prep.fhir");
    	pum.setDefaultPersistenceUnitName(defaultPersistenceUnitName);
    	//pum.setPersistenceXmlLocations("classpath:/META-INF/persistence.xml");
    	pum.setMappingResources(orm);
    	pum.setDefaultDataSource(dataSource());

    	return pum;
    }*/

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
		//retVal.setPersistenceUnitManager(persistenceUnitManager());
		retVal.setPackagesToScan("org.partners.usciitg_prep.terminology.jpa","org.partners.usciitg_prep.fhir");		
		retVal.setMappingResources(orm);
		retVal.setPersistenceUnitName(defaultPersistenceUnitName);
		retVal.setDataSource(dataSource());
		retVal.setPersistenceProvider(new HibernatePersistenceProvider());		
		retVal.setJpaProperties(jpaProperties());
		return retVal;
	}

	private Properties jpaProperties() {
		Properties extraProperties = new Properties();
		extraProperties.put("hibernate.dialect", hibernateDialect);
		extraProperties.put("hibernate.format_sql", hibernateFormatSql);
		extraProperties.put("hibernate.show_sql", hibernateShowSql);
		extraProperties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
		extraProperties.put("hibernate.jdbc.batch_size", hibernateJdbcBatchSize);
		extraProperties.put("hibernate.cache.use_query_cache", hibernateCacheUseQueryCache);
		extraProperties.put("hibernate.cache.use_second_level_cache", hibernateCacheUseSecondLevelCache);
		extraProperties.put("hibernate.cache.use_structured_entries", hibernateCacheUseStructuredEntries);
		extraProperties.put("hibernate.cache.use_minimal_puts", hibernateCacheUseMinimalPuts);
		extraProperties.put("hibernate.search.default.directory_provider", hibernateSearchDefaultDirectoryProvider);
		extraProperties.put("hibernate.search.default.indexBase", hibernateSearchDefaultIndexBase);
		extraProperties.put("hibernate.search.lucene_version", hibernateSearchLuceneVersion);
//		extraProperties.put("hibernate.search.default.worker.execution", "async");
		return extraProperties;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager retVal = new JpaTransactionManager();
		retVal.setEntityManagerFactory(entityManagerFactory);
		return retVal;
	}
}

