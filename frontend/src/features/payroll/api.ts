import { apiFetch } from '../../shared/api/client';

export interface PayPeriod {
  id: string;
  startDate: string;
  endDate: string;
  state: string;
  version: number;
  closedBy: string | null;
  closedAt: string | null;
}

export interface PayrollReadiness {
  totalEmployees: number;
  unresolvedCount: number;
  totalRegularMinutes: number;
  totalOvertimeMinutes: number;
  finalizedPercent: number;
}

export interface PayrollExport {
  id: string;
  version: number;
  checksum: string;
  format: string;
  generatedAt: string;
  generatedBy: string;
}

export function listPayPeriods(): Promise<PayPeriod[]> {
  return apiFetch<PayPeriod[]>('/pay-periods');
}

export function openPayPeriod(startDate: string, endDate: string): Promise<PayPeriod> {
  return apiFetch<PayPeriod>('/pay-periods', {
    method: 'POST',
    body: JSON.stringify({ startDate, endDate }),
  });
}

export function getReadiness(id: string): Promise<PayrollReadiness> {
  return apiFetch<PayrollReadiness>(`/pay-periods/${id}/readiness`);
}

export function closePeriod(id: string): Promise<PayPeriod> {
  return apiFetch<PayPeriod>(`/pay-periods/${id}/close`, { method: 'POST' });
}

export function reopenPeriod(id: string, reason: string): Promise<PayPeriod> {
  return apiFetch<PayPeriod>(`/pay-periods/${id}/reopen`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  });
}

export function exportPeriod(id: string): Promise<PayrollExport> {
  return apiFetch<PayrollExport>(`/pay-periods/${id}/exports`, { method: 'POST' });
}

export function listExports(id: string): Promise<PayrollExport[]> {
  return apiFetch<PayrollExport[]>(`/pay-periods/${id}/exports`);
}
