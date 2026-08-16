import { apiFetch } from '../../shared/api/client';

export interface AttendanceRecord {
  id: string;
  employeeId: string;
  businessDate: string;
  status: string;
  regularMinutes: number;
  overtimeMinutes: number;
  breakMinutes: number;
  workedMinutes: number;
  version: number;
  scheduleEntryId: string | null;
}

export interface AttendanceException {
  recordId: string;
  type: string;
  severity: string;
  state: string;
  detail: string;
  createdAt: string;
}

export function listAttendance(): Promise<AttendanceRecord[]> {
  return apiFetch<AttendanceRecord[]>('/attendance');
}

export function listExceptions(): Promise<AttendanceException[]> {
  return apiFetch<AttendanceException[]>('/attendance/exceptions');
}

export function submitCorrection(id: string, reason: string): Promise<{ caseId: string }> {
  return apiFetch<{ caseId: string }>(`/attendance/${id}/corrections`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  });
}
