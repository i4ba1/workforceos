import { apiFetch } from '../../shared/api/client';

export interface ShiftTemplate {
  id: string;
  name: string;
  localStart: string;
  localEnd: string;
  zoneId: string;
  breakMinutes: number;
  breakPaid: boolean;
}

export interface CreateShiftTemplateInput {
  name: string;
  localStart: string;
  localEnd: string;
  zoneId: string;
  breakMinutes: number;
}

export interface ScheduleEntry {
  id: string;
  employeeId: string;
  businessDate: string;
  zoneId: string;
  plannedStart: string;
  plannedEnd: string;
  version: number;
}

export interface CreateScheduleEntryInput {
  employeeId: string;
  businessDate: string;
  zoneId: string;
  plannedStart: string;
  plannedEnd: string;
}

export function listShiftTemplates(): Promise<ShiftTemplate[]> {
  return apiFetch<ShiftTemplate[]>('/shift-templates');
}

export function createShiftTemplate(input: CreateShiftTemplateInput): Promise<ShiftTemplate> {
  return apiFetch<ShiftTemplate>('/shift-templates', {
    method: 'POST',
    body: JSON.stringify(input),
  });
}

export function createScheduleEntry(input: CreateScheduleEntryInput): Promise<ScheduleEntry> {
  return apiFetch<ScheduleEntry>('/schedule-entries', {
    method: 'POST',
    body: JSON.stringify(input),
  });
}
