import { apiFetch } from '../../shared/api/client';

export type TimeEventType = 'CLOCK_IN' | 'CLOCK_OUT' | 'BREAK_START' | 'BREAK_END';

export interface TimeEvent {
  id: string;
  employeeId: string;
  eventType: string;
  occurredAt: string;
  receivedAt: string;
  zoneId: string;
  source: string;
  sourceEventId: string | null;
}

export interface RecordTimeEventInput {
  employeeId: string;
  eventType: TimeEventType;
  occurredAt: string;
  zoneId: string;
}

export function recordTimeEvent(input: RecordTimeEventInput, idempotencyKey: string): Promise<TimeEvent> {
  return apiFetch<TimeEvent>('/time-events', {
    method: 'POST',
    headers: { 'Idempotency-Key': idempotencyKey },
    body: JSON.stringify(input),
  });
}

export function listTimeEvents(employeeId: string, from: string, to: string): Promise<TimeEvent[]> {
  return apiFetch<TimeEvent[]>(
    `/employees/${employeeId}/time-events?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`,
  );
}
