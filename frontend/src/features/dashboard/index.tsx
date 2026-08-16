import { useQuery } from '@tanstack/react-query';
import { Alert, Box, Card, CardContent, CircularProgress, Typography } from '@mui/material';
import { listAttendance, listExceptions } from '../attendance/api';
import { listApprovalCases } from '../approvals/api';

function StatCard({ label, value }: { label: string; value: number }) {
  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="overline" color="text.secondary">
          {label}
        </Typography>
        <Typography variant="h4">{value}</Typography>
      </CardContent>
    </Card>
  );
}

export default function DashboardPage() {
  const attendance = useQuery({ queryKey: ['attendance'], queryFn: listAttendance });
  const exceptions = useQuery({ queryKey: ['exceptions'], queryFn: listExceptions });
  const approvals = useQuery({ queryKey: ['approval-cases'], queryFn: listApprovalCases });

  const loading = attendance.isLoading || exceptions.isLoading || approvals.isLoading;
  const error = attendance.isError || exceptions.isError || approvals.isError;

  const records = attendance.data ?? [];
  const openExceptions = (exceptions.data ?? []).length;
  const openApprovals = (approvals.data ?? []).length;
  const lateToday = records.filter((r) => r.status === 'LATE').length;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Dashboard
      </Typography>
      {loading ? (
        <CircularProgress />
      ) : error ? (
        <Alert severity="error">Failed to load dashboard metrics.</Alert>
      ) : (
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: 'repeat(4, 1fr)' }, gap: 2 }}>
          <StatCard label="Attendance records" value={records.length} />
          <StatCard label="Open exceptions" value={openExceptions} />
          <StatCard label="Open approvals" value={openApprovals} />
          <StatCard label="Late (current data)" value={lateToday} />
        </Box>
      )}
    </Box>
  );
}
