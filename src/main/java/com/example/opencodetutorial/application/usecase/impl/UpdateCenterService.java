package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateCenterUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateCenterCommand;
import com.example.opencodetutorial.domain.exception.CenterNotFoundException;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CenterName;
import com.example.opencodetutorial.domain.port.CenterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UpdateCenterUseCase} implementation. Loads the existing aggregate,
 * checks the name is still unique across the system, asks the aggregate to apply the
 * new name and hands it back to the repository.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateCenterService implements UpdateCenterUseCase {

    private final CenterRepository centerRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param centerRepository the center repository, must not be null
     */
    public UpdateCenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional
    public Center execute(UpdateCenterCommand command) {
        CenterId id = new CenterId(command.id());
        Center existing = centerRepository.findById(id)
                .orElseThrow(() -> new CenterNotFoundException(id));

        CenterName newName = new CenterName(command.name());
        boolean takenByOther = centerRepository.findAll().stream()
                .anyMatch(center -> center.name().equals(newName) && !center.id().equals(id));
        if (takenByOther) {
            throw new DuplicateNameException("center", newName.value(), "in the system");
        }

        existing.rename(newName);
        return centerRepository.save(existing);
    }
}