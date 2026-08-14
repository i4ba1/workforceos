import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from './AppShell';
import DashboardPage from '../features/dashboard';
import PeoplePage from '../features/people';
import SchedulingPage from '../features/scheduling';
import TimeClockPage from '../features/timeclock';
import AttendancePage from '../features/attendance';
import PayrollPage from '../features/payroll';
import PoliciesPage from '../features/policies';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'people', element: <PeoplePage /> },
      { path: 'schedule', element: <SchedulingPage /> },
      { path: 'time', element: <TimeClockPage /> },
      { path: 'attendance', element: <AttendancePage /> },
      { path: 'payroll', element: <PayrollPage /> },
      { path: 'policies', element: <PoliciesPage /> },
    ],
  },
]);
