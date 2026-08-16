import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { listAttendance, listExceptions, submitCorrection, type AttendanceRecord } from './api';

export default function AttendancePage() {
  const queryClient = useQueryClient();
  const { data: records = [], isLoading, isError } = useQuery({
    queryKey: ['attendance'],
    queryFn: listAttendance,
  });
  const { data: exceptions = [] } = useQuery({ queryKey: ['exceptions'], queryFn: listExceptions });

  const [correctionFor, setCorrectionFor] = useState<AttendanceRecord | null>(null);
  const [reason, setReason] = useState('');

  const mutation = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) => submitCorrection(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['approval-cases'] });
      setCorrectionFor(null);
      setReason('');
    },
  });

  const confirm = () => {
    if (correctionFor && reason.trim()) {
      mutation.mutate({ id: correctionFor.id, reason: reason.trim() });
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Attendance
      </Typography>

      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {mutation.error.message}
        </Alert>
      )}
      {mutation.isSuccess && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Correction request submitted.
        </Alert>
      )}

      <Typography variant="h6" gutterBottom>
        Records
      </Typography>
      {isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load attendance records.</Alert>
      ) : (
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Employee</TableCell>
              <TableCell>Date</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Worked</TableCell>
              <TableCell>Overtime</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {records.map((record) => (
              <TableRow key={record.id}>
                <TableCell>{record.employeeId}</TableCell>
                <TableCell>{record.businessDate}</TableCell>
                <TableCell>{record.status}</TableCell>
                <TableCell>{record.workedMinutes}m</TableCell>
                <TableCell>{record.overtimeMinutes}m</TableCell>
                <TableCell align="right">
                  <Button size="small" onClick={() => setCorrectionFor(record)}>
                    Correct
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {records.length === 0 && (
              <TableRow>
                <TableCell colSpan={6}>No attendance records yet.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}

      <Typography variant="h6" sx={{ mt: 3 }} gutterBottom>
        Open exceptions
      </Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Record</TableCell>
            <TableCell>Type</TableCell>
            <TableCell>Severity</TableCell>
            <TableCell>Detail</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {exceptions.map((exception) => (
            <TableRow key={`${exception.recordId}-${exception.type}`}>
              <TableCell>{exception.recordId}</TableCell>
              <TableCell>{exception.type}</TableCell>
              <TableCell>{exception.severity}</TableCell>
              <TableCell>{exception.detail}</TableCell>
            </TableRow>
          ))}
          {exceptions.length === 0 && (
            <TableRow>
              <TableCell colSpan={4}>No open exceptions.</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <Dialog open={correctionFor !== null} onClose={() => setCorrectionFor(null)} fullWidth maxWidth="sm">
        <DialogTitle>Submit correction</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Request a correction for the attendance record of {correctionFor?.businessDate}.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            label="Reason"
            fullWidth
            value={reason}
            onChange={(event) => setReason(event.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCorrectionFor(null)}>Cancel</Button>
          <Button onClick={confirm} disabled={!reason.trim() || mutation.isPending}>
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
