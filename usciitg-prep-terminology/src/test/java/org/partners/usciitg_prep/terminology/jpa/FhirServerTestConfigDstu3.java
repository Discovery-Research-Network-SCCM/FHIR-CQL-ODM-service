package org.partners.usciitg_prep.terminology.jpa;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;

@Configuration
@EnableTransactionManagement()
@ComponentScan(basePackages="org.partners.usciitg_prep.terminology.jpa")
@PropertySource("classpath:/org/partners/usciitg_prep/terminology/jpa/jpaApplicationTest.properties")
public class FhirServerTestConfigDstu3  {

	@Value("${jndi.datasource}")
	private String jndiDatasource;
	
	@Value("${spring.jpa.orm}")
	private String orm;
	@Value("${spring.jpa.persistenceUnitName}")
	private String persistenceUnitName;
	
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

	/**
	 * The following bean configures the database connection. The 'url' property value of "jdbc:derby:directory:jpaserver_derby_files;create=true" indicates that the server should save resources in a
	 * directory called "jpaserver_derby_files".
	 * 
	 * A URL to a remote database could also be placed here, along with login credentials and other properties supported by BasicDataSource.
	 */
//	@Bean//(destroyMethod = "close")
//	public DataSource dataSource() {
//		DriverManagerDataSource retVal = new DriverManagerDataSource();
//		retVal.setDriverClassName("com.mysql.jdbc.Driver");
//		retVal.setUrl("jdbc:mysql://phspulse4:3306/usciitg");
//		retVal.setUsername("redcap");
//		retVal.setPassword("r1e2d3c4ap");
//		return retVal;
//	}
	
	@Bean
    public DataSource dataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        DataSource dataSource = dsLookup.getDataSource(jndiDatasource);
        return dataSource;
    } 

/*    @Bean
    public PersistenceUnitManager persistenceUnitManager() {
    	DefaultPersistenceUnitManager pum = new DefaultPersistenceUnitManager();
    	pum.setPackagesToScan("org.partners.usciitg_prep.terminology.jpa");
    	pum.setDefaultPersistenceUnitName(persistenceUnitName);
    	//pum.setPersistenceXmlLocations("classpath:/META-INF/persistence.xml");
    	pum.setMappingResources(orm);
    	pum.setDefaultDataSource(dataSource());

    	return pum;
    }*/
    
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean retVal = new LocalContainerEntityManagerFactoryBean();
		//retVal.setPersistenceUnitManager(persistenceUnitManager());
		retVal.setMappingResources(orm);
		retVal.setPackagesToScan("org.partners.usciitg_prep.terminology.jpa");
		retVal.setPersistenceUnitName(persistenceUnitName);
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

	/**
	 * Do some fancy logging to create a nice access log that has details about each incoming request.
	 */
	public IServerInterceptor loggingInterceptor() {
		LoggingInterceptor retVal = new LoggingInterceptor();
		retVal.setLoggerName("fhirtest.access");
		retVal.setMessageFormat(
				"Path[${servletPath}] Source[${requestHeader.x-forwarded-for}] Operation[${operationType} ${operationName} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] ResponseEncoding[${responseEncodingNoDefault}]");
		retVal.setLogExceptions(true);
		retVal.setErrorMessageFormat("ERROR - ${requestVerb} ${requestUrl}");
		return retVal;
	}

	/**
	 * This interceptor adds some pretty syntax highlighting in responses when a browser is detected
	 */
/*	@Bean(autowire = Autowire.BY_TYPE)
	public IServerInterceptor responseHighlighterInterceptor() {
		ResponseHighlighterInterceptor retVal = new ResponseHighlighterInterceptor();
		return retVal;
	}*/

	/*	@Bean(autowire = Autowire.BY_TYPE)
	public IServerInterceptor subscriptionSecurityInterceptor() {
		SubscriptionsRequireManualActivationInterceptorDstu3 retVal = new SubscriptionsRequireManualActivationInterceptorDstu3();
		return retVal;
	}*/

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager retVal = new JpaTransactionManager();
		retVal.setEntityManagerFactory(entityManagerFactory);
		return retVal;
	}

}

