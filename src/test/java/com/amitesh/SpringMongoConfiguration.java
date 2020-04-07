package com.amitesh;

/**
 * Created by Amitesh on 10-02-2017.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * This class contains configuration details of Testing DB uded during Unit testing
 */

@Configuration
@EnableMongoRepositories
public class SpringMongoConfiguration extends AbstractMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "gojektest";
	}

	@Bean
	public MongoClient mongo() throws Exception {
		return new MongoClient(new MongoClientURI( "mongodb://testinguser:testinguser123@localhost:27017/gojektest" ));
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), getDatabaseName());
	}

}
