import { apiFetch } from '../../shared/api/client';

export interface ApprovalCase {
  id: string;
  subjectType: string;
  subjectId: string;
  state: string;
  version: number;
  reason: string;
  openedAt: string;
  openedBy: string;
}

export function listApprovalCases(): Promise<ApprovalCase[]> {
  return apiFetch<ApprovalCase[]>('/approval-cases');
}

export function approveCase(id: string, expectedVersion: number, reason: string): Promise<ApprovalCase> {
  return apiFetch<ApprovalCase>(`/approval-cases/${id}/approve`, {
    method: 'POST',
    body: JSON.stringify({ expectedVersion, reason }),
  });
}

export function rejectCase(id: string, expectedVersion: number, reason: string): Promise<ApprovalCase> {
  return apiFetch<ApprovalCase>(`/approval-cases/${id}/reject`, {
    method: 'POST',
    body: JSON.stringify({ expectedVersion, reason }),
  });
}
