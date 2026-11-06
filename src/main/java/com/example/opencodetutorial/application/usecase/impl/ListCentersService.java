package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListCentersUseCase;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.port.CenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListCentersUseCase} implementation.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListCentersService implements ListCentersUseCase {

    private final CenterRepository centerRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param centerRepository the center repository, must not be null
     */
    public ListCentersService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Center> execute() {
        return centerRepository.findAll();
    }
}