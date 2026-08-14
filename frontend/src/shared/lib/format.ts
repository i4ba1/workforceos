/**
 * Pure formatting helpers. No feature business rules live here.
 *
 * <p>Date/time helpers always take an explicit IANA zone; the browser time zone is never
 * assumed to equal the employee/business time zone.</p>
 */

export function formatInstant(
  instant: string,
  zone: string,
  options?: Intl.DateTimeFormatOptions,
): string {
  const date = new Date(instant);
  return new Intl.DateTimeFormat('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short',
    timeZone: zone,
    ...options,
  }).format(date);
}

export function formatMinutes(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}h ${mins.toString().padStart(2, '0')}m`;
}

/**
 * Converts an HTML datetime-local value to an ISO-8601 instant, treating the entered
 * wall-clock as UTC. This is a Phase-1 demo simplification; time-zone-correct
 * conversion lands with the attendance engine.
 */
export function datetimeLocalToInstant(local: string): string {
  return new Date(`${local}:00Z`).toISOString();
}
