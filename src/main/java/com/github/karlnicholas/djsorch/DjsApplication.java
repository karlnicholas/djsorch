package com.github.karlnicholas.djsorch;

import java.sql.Statement;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.karlnicholas.djsorch.processor.TransactionProcessorJava;
import com.github.karlnicholas.djsorch.queue.SubjectQueueManager;

@SpringBootApplication
public class DjsApplication implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(DjsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DjsApplication.class, args);
	}

	private final DataSource dataSource; 
	private final SubjectQueueManager subjectQueueManager;
	private final TransactionProcessorJava transactionProcessor;

	public DjsApplication(
			DataSource dataSource, 
			SubjectQueueManager subjectQueueManager, 
			TransactionProcessorJava transactionProcessor
	) {
		this.dataSource = dataSource;
		this.subjectQueueManager = subjectQueueManager;
		this.transactionProcessor = transactionProcessor;
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		subjectQueueManager.addPostMethod("transaction", transactionProcessor::handleTransaction);
		try ( Statement statement = dataSource.getConnection().createStatement() ) {
			statement.execute("create sequence account_seq start with 1000 increment by 1");
			statement.execute("create sequence loan_seq start with 1000 increment by 1");
			statement.execute("create table account (id bigint identity, open_date date, primary key (id))");
			statement.execute("create table account_closed (id bigint not null, open_date date, original_id bigint, primary key (id))");
			statement.execute("create table loan (id bigint identity, fixed_mindue decimal(19,2), inception_date date, interest_rate decimal(19,2), principal decimal(19,2), term_months integer, account_id bigint, primary key (id))");
			statement.execute("create table loan_closed (id bigint not null, fixed_mindue decimal(19,2), inception_date date, interest_rate decimal(19,2), principal decimal(19,2), term_months integer, account_closed_id bigint, primary key (id))");
			statement.execute("create table transaction_closed (id bigint not null, business_date date, payload varchar(4000), transaction_date date, type varchar(255), version bigint, account_closed_id bigint, primary key (id))");
			statement.execute("create table transaction_open (id bigint identity, business_date date, payload varchar(4000), transaction_date date, type varchar(255), version bigint, account_id bigint, primary key (id))");
			statement.execute("create table transaction_submitted (id bigint identity, business_date date, payload varchar(4000), transaction_date date, type varchar(255), version bigint, account_id bigint, primary key (id))");
			statement.execute("create table transaction_rejected (id bigint not null, business_date date, payload varchar(4000), transaction_date date, type varchar(255), version bigint, account_id bigint, primary key (id))");
			statement.execute("create index IDX8hl04kre9pgr0b9b7r5jipqra on account_closed (original_id)");
			statement.execute("create index IDXfjs1q0rt6k8jcu5gltousur8y on transaction_open (account_id, type)");
			statement.execute("alter table loan add constraint FKnbbh9l71cf3hk76mvmjjfn7n5 foreign key (account_id) references account");
			statement.execute("alter table loan_closed add constraint FKpgpt07xu8o7gp4p56up2rf2f8 foreign key (account_closed_id) references account_closed");
			statement.execute("alter table transaction_closed add constraint FKevvq0jionbirjxljl0y736k53 foreign key (account_closed_id) references account_closed");
			statement.execute("alter table transaction_open add constraint FKsy6domeeutsrpk8oe5vg15s2t foreign key (account_id) references account");
			statement.execute("alter table transaction_submitted add constraint FKq4kgxmb4wd1mfe4wj825drqks foreign key (account_id) references account");
		}

		System.out.println("Done DB Build");
	}
}
