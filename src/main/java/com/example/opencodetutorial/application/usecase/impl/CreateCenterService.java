package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateCenterUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateCenterCommand;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CenterName;
import com.example.opencodetutorial.domain.port.CenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CreateCenterUseCase} implementation.
 * Maps the raw command into the domain, checks the system-wide name uniqueness and
 * hands the aggregate to the repository, which is responsible for assigning an identifier.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateCenterService implements CreateCenterUseCase {

    private final CenterRepository centerRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param centerRepository the center repository, must not be null
     */
    public CreateCenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional
    public CenterId execute(CreateCenterCommand command) {
        CenterName name = new CenterName(command.name());
        boolean taken = centerRepository.findAll().stream()
                .anyMatch(center -> center.name().equals(name));
        if (taken) {
            throw new DuplicateNameException("center", name.value(), "in the system");
        }
        Center center = Center.create(name);
        return centerRepository.save(center).id();
    }
}