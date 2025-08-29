package com.carloscoral.r2dbc;

import com.carloscoral.model.loanstatus.LoanStatus;
import com.carloscoral.model.loanstatus.gateways.LoanStatusRepository;
import com.carloscoral.r2dbc.entity.LoanStatusEntity;
import com.carloscoral.r2dbc.helper.ReactiveAdapterOperations;

import reactor.core.publisher.Mono;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class LoanStatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanStatus,
        LoanStatusEntity,
        UUID,
        LoanStatusReactiveRepository
> implements LoanStatusRepository {
    public LoanStatusReactiveRepositoryAdapter(LoanStatusReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d, LoanStatus.LoanStatusBuilder.class).build());
    }

    @Override
    public Mono<LoanStatus> findByName(String name) {
        return repository.findByName(name)
                .map(d -> mapper.mapBuilder(d, LoanStatus.LoanStatusBuilder.class).build());
    }
}
