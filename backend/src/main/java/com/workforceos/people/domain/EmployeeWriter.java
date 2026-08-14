package com.workforceos.people.domain;

/** Write-side port for employee and assignment persistence. */
public interface EmployeeWriter {

    Employee save(Employee employee);

    void saveAssignment(EmploymentAssignment assignment);
}
