import { apiFetch } from '../../shared/api/client';

export interface Employee {
  id: string;
  employeeNo: string;
  firstName: string;
  lastName: string;
  email: string | null;
  status: string;
}

export interface CreateEmployeeInput {
  employeeNo: string;
  firstName: string;
  lastName: string;
  email?: string;
}

export function listEmployees(): Promise<Employee[]> {
  return apiFetch<Employee[]>('/employees');
}

export function createEmployee(input: CreateEmployeeInput): Promise<Employee> {
  return apiFetch<Employee>('/employees', {
    method: 'POST',
    body: JSON.stringify(input),
  });
}
