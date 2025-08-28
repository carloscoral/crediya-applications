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

    private final LoanTypeReactiveRepository loanTypeRepository;
    private final LoanStatusReactiveRepository loanStatusRepository;

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
        this.loanTypeRepository = loanTypeRepository;
        this.loanStatusRepository = loanStatusRepository;
    }

    @Transactional
    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return Mono.zip(
                loanTypeRepository.findByName(loanApplication.getLoanType().getName())
                        .switchIfEmpty(Mono.error(new RuntimeException("LoanType not found: " + loanApplication.getLoanType().getName()))),
                loanStatusRepository.findByName(loanApplication.getLoanStatus().getName())
                        .switchIfEmpty(Mono.error(new RuntimeException("LoanStatus not found: " + loanApplication.getLoanStatus().getName())))
        )
        .flatMap(tuple -> {
            LoanApplicationEntity entity = LoanApplicationEntity.builder()
                    .amount(loanApplication.getAmount())
                    .monthsTerm(loanApplication.getMonthsTerm())
                    .email(loanApplication.getEmail())
                    .loanTypeId(tuple.getT1().getId())
                    .loanStatusId(tuple.getT2().getId())
                    .build();
            
            return repository.save(entity);
        })
        .map(this::toEntity);
    }

}
