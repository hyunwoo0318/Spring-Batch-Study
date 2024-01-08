package com.example.springbatchstudy.batch;

import com.example.springbatchstudy.domain.Dept;
import com.example.springbatchstudy.domain.Dept2;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPageJob1 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final int chunkSize = 10;

    @Bean
    public Job JpaPageJob1_batchBuild(){
        return jobBuilderFactory.get("taskletJob2")
            .start(JpaPageJob1_step1()).build();
    }

    @Bean
    public Step JpaPageJob1_step1(){
        return stepBuilderFactory.get("jpaPageJob1_step1")
            .<Dept, Dept2>chunk(chunkSize)
            .reader(jpaPageJob1_DBItemReader())
            .processor(jpaPageJob1_processor())
            .writer(jpaPageJob1_DBItemWriter())
            .build();
    }

    private ItemProcessor<Dept, Dept2> jpaPageJob1_processor() {
        return dept -> {
            return new Dept2(dept.getDeptNo(), "NEW_"+ dept.getDName(), "NEW_"+dept.getLoc());
        };
    }

    @Bean
    @JobScope
    public Step JpaPageJob1_step2(@Value("#{jobParameters['date']}") String date){
        return stepBuilderFactory.get("Job Step1")
            .tasklet((t, c) -> {
                log.debug("-> step1 -> [step2]" + date);
                return RepeatStatus.FINISHED;
            }).build();
    }

    @Bean(destroyMethod = "")
    public JpaPagingItemReader<Dept> jpaPageJob1_DBItemReader(){
        System.out.println("READER START ---------------------");
        return new JpaPagingItemReaderBuilder<Dept>()
            .name("jpaPageJob1_DBItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("SELECT d FROM Dept d")
            .build();
    }

    @Bean
    public ItemWriter<Dept> jpaPageJob1_printItemWriter(){
        System.out.println("writer start!!!!----------------------");
        return list ->{
            for (Dept dept : list) {
                log.debug(dept.toString());
            }
        };
    }

    @Bean
    public JpaItemWriter<Dept2> jpaPageJob1_DBItemWriter(){
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
