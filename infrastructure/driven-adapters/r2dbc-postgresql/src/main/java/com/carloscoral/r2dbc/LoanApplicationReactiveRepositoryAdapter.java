package com.carloscoral.r2dbc;

import com.carloscoral.model.loanapplication.LoanApplication;
import com.carloscoral.model.loanapplication.gateways.LoanApplicationRepository;
import com.carloscoral.r2dbc.entity.LoanApplicationEntity;
import com.carloscoral.r2dbc.helper.ReactiveAdapterOperations;

import reactor.core.publisher.Mono;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        UUID,
        LoanApplicationReactiveRepository
> implements LoanApplicationRepository {

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, 
                                                  ObjectMapper mapper,
                                                  LoanTypeReactiveRepository loanTypeRepository,
                                                  LoanStatusReactiveRepository loanStatusRepository) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d, LoanApplication.LoanApplicationBuilder.class).build());
    }

    @Transactional
    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return repository.save(mapper.mapBuilder(
                loanApplication,
                LoanApplicationEntity.LoanApplicationEntityBuilder.class
                ).build())
                .map(entity -> mapper.mapBuilder(entity, LoanApplication.LoanApplicationBuilder.class).build());
    }

}
