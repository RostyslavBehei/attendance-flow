package com.attendance.flow.service.impl;

import com.attendance.flow.exception.AlreadyExistsException;
import com.attendance.flow.exception.NotFoundException;
import com.attendance.flow.model.Subject;
import com.attendance.flow.model.dto.subject.SubjectCreateRequest;
import com.attendance.flow.model.dto.subject.SubjectResponse;
import com.attendance.flow.repository.SubjectRepository;
import com.attendance.flow.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(subject -> new SubjectResponse(
                        subject.getId(),
                        subject.getName(),
                        subject.getDescription())
                ).toList();
    }

    @Override
    @Transactional
    public SubjectResponse createSubject(SubjectCreateRequest request) {
        if (subjectRepository.existsByNameIgnoreCase(request.name())) {
            throw new AlreadyExistsException("Subject with name " + request.name() + " already exists");
        }

        Subject newSubject = Subject.builder()
                .name(request.name())
                .description(request.description())
                .build();

        Subject savedSubject = subjectRepository.save(newSubject);

        return new SubjectResponse(
                savedSubject.getId(),
                savedSubject.getName(),
                savedSubject.getDescription()
        );
    }

    @Override
    @Transactional
    public void deleteSubject(Long subjectId) {
        if (subjectRepository.existsById(subjectId)) {
            subjectRepository.deleteById(subjectId);
        } else {
            throw new NotFoundException("Subject with id " + subjectId + " not found");
        }
    }
}
