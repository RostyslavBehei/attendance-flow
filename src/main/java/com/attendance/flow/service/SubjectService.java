package com.attendance.flow.service;

import com.attendance.flow.model.dto.subject.SubjectCreateRequest;
import com.attendance.flow.model.dto.subject.SubjectResponse;

import java.util.List;

public interface SubjectService {
    List<SubjectResponse> getAllSubjects();
    SubjectResponse createSubject(SubjectCreateRequest request);
    void deleteSubject(Long subjectId);
}
