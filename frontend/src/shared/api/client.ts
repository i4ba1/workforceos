/**
 * Typed API transport boundary.
 *
 * <p>Server-state calls go through this client so error mapping and auth handling stay
 * in one place, separate from business validation. Placeholder until the backend
 * contract is generated from OpenAPI.</p>
 */

export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail?: string;
  code?: string;
  correlationId?: string;
}

export class ApiError extends Error {
  readonly status: number;
  readonly problem: ProblemDetail;

  constructor(problem: ProblemDetail) {
    super(problem.detail ?? problem.title ?? 'Request failed');
    this.name = 'ApiError';
    this.status = problem.status;
    this.problem = problem;
  }
}

/**
 * Demo tenant used during local development. In production the tenant is derived from
 * the authenticated OIDC claim, never from a client-controlled header.
 */
export const DEMO_TENANT_ID = '00000000-0000-0000-0000-000000000001';

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api/v1';

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      'X-Tenant-Id': DEMO_TENANT_ID,
      ...init?.headers,
    },
  });

  if (!response.ok) {
    const problem = (await response.json().catch(() => null)) as ProblemDetail | null;
    throw new ApiError(
      problem ?? { type: 'about:blank', title: response.statusText, status: response.status },
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}
