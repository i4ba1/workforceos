import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const TENANT = '00000000-0000-0000-0000-000000000001';

export const options = {
  scenarios: {
    attendance_read: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 20 },
        { duration: '30s', target: 20 },
        { duration: '10s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<400'],
    http_req_failed: ['rate<0.01'],
  },
};

const headers = {
  'Content-Type': 'application/json',
  'X-Tenant-Id': TENANT,
};

export default function () {
  // Read path
  const listRes = http.get(`${BASE_URL}/attendance`, { headers });
  check(listRes, { 'attendance 200': (r) => r.status === 200 });

  // Clock ingestion with occasional duplicate idempotency key (exercises dedup)
  const idempotencyKey = Math.random() < 0.05 ? 'dup-fixed-key' : crypto.randomUUID();
  const clockRes = http.post(
    `${BASE_URL}/time-events`,
    JSON.stringify({
      employeeId: '00000000-0000-0000-0000-000000000102',
      eventType: 'CLOCK_IN',
      occurredAt: new Date().toISOString(),
      zoneId: 'Asia/Jakarta',
    }),
    { headers: { ...headers, 'Idempotency-Key': idempotencyKey } },
  );
  check(clockRes, { 'clock 2xx/409': (r) => r.status === 200 || r.status === 201 || r.status === 409 });

  sleep(0.5);
}
