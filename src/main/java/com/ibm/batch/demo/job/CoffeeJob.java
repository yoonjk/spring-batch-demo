package com.ibm.batch.demo.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ibm.batch.demo.processor.CoffeeItemProcessor;
import com.ibm.batch.demo.vo.CoffeeVO;

@Configuration
@EnableBatchProcessing
public class CoffeeJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    
    @Bean
    @StepScope
    public FlatFileItemReader<CoffeeVO> reader(@Value("#{JobParameters[filename]}") String filename) {
        
        return new FlatFileItemReaderBuilder<CoffeeVO>().name("coffeeItemReader")
            .resource(new ClassPathResource(filename))
            .delimited()
            .names(new String[] { "brand", "origin", "characteristics" })
            .fieldSetMapper(new BeanWrapperFieldSetMapper<CoffeeVO>() {{
                setTargetType(CoffeeVO.class);
             }})
            .build();
    }
    
    @Bean
    public CoffeeItemProcessor processor() {
        return new CoffeeItemProcessor();
    }
    
    @Bean
    public JdbcBatchItemWriter<CoffeeVO> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CoffeeVO>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
            .dataSource(dataSource)
            .build();
    }
    
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
    	return jobBuilderFactory.get("importUserJob")
    			.incrementer(new RunIdIncrementer())
    			.flow(step1)
    			.end()
    			.build();
    }
    
    @Bean
    public Step step1(JdbcBatchItemWriter<CoffeeVO> writer) {
        return stepBuilderFactory.get("step1")
            .<CoffeeVO, CoffeeVO> chunk(10)
            .reader(reader(null))
            .processor(processor())
            .writer(writer)
            .build();
    }
}
