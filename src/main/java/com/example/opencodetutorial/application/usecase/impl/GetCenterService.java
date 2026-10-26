package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetCenterUseCase;
import com.example.opencodetutorial.domain.exception.CenterNotFoundException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.port.CenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetCenterUseCase} implementation. Surfaces a missing center as a
 * domain exception so the REST layer can map it to HTTP 404.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetCenterService implements GetCenterUseCase {

    private final CenterRepository centerRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param centerRepository the center repository, must not be null
     */
    public GetCenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Center execute(CenterId id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new CenterNotFoundException(id));
    }
}