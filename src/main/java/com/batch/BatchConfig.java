package com.batch;

import java.util.concurrent.Flow.Processor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.batch.entity.Customer;
import com.batch.repo.CustomerRepo;

import lombok.AllArgsConstructor;

@EnableBatchProcessing
@Configuration
@AllArgsConstructor
public class BatchConfig {
	
	@Autowired
	CustomerRepo crepo;
	
	
	@Bean
	public FlatFileItemReader<Customer> reader() {
	  return new FlatFileItemReaderBuilder<Customer>()
	    .name("personItemReader")
	    .resource(new ClassPathResource("sample-data.csv"))
	    .delimited()
	    .names("firstName", "lastName","email","gender","contactNo","country","dob")
	    .targetType(Customer.class)
	    .build();
	}

	
	@Bean
	CustomerProcessor customerProcessor()
	{
		return new CustomerProcessor();
	}
	
	@Bean
	public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
	  return new JdbcBatchItemWriterBuilder<Customer>()
	    .sql("INSERT INTO customers (first_name, last_name,eamil,gender,contact_no,country,dob) VALUES (:firstName, :lastName)")
	    .dataSource(dataSource)
	    .beanMapped()
	    .build();
	}
	
	@Bean
	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
	          FlatFileItemReader<Customer> reader, CustomerProcessor processor, JdbcBatchItemWriter<Customer> writer) {
	  return new StepBuilder("step1", jobRepository)
	    .<Customer, Customer> chunk(3, transactionManager)
	    .reader(reader)
	    .processor(processor)
	    .writer(writer)
	    .build();
	}
	
	@Bean
	public Job importUserJob(JobRepository jobRepository,Step step1, JobCompletionNotificationListener listener) {
	  return new JobBuilder("importUserJob", jobRepository)
	    .listener(listener)
	    .start(step1)
	    .build();
	}
	
}
