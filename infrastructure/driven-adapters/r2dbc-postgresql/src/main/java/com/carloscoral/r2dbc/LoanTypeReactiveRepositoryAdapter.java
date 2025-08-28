package com.carloscoral.r2dbc;

import com.carloscoral.model.loantype.LoanType;
import com.carloscoral.model.loantype.gateways.LoanTypeRepository;
import com.carloscoral.r2dbc.entity.LoanTypeEntity;
import com.carloscoral.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        UUID,
        LoanTypeReactiveRepository
> implements LoanTypeRepository {
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.mapBuilder(d, LoanType.LoanTypeBuilder.class).build());
    }

    @Override
    public Mono<LoanType> findById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return super.findById(uuid);
        } catch (IllegalArgumentException e) {
            return Mono.empty();
        }
    }

}
