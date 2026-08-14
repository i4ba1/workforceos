import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  MenuItem,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { listEmployees } from '../people/api';
import { listTimeEvents, recordTimeEvent, type TimeEventType } from './api';
import { formatInstant } from '../../shared/lib/format';

const DAY_MS = 24 * 60 * 60 * 1000;

export default function TimeClockPage() {
  const queryClient = useQueryClient();
  const [employeeId, setEmployeeId] = useState('');
  const [zoneId, setZoneId] = useState('Asia/Jakarta');

  const { data: employees = [] } = useQuery({ queryKey: ['employees'], queryFn: listEmployees });

  const from = new Date(Date.now() - 30 * DAY_MS).toISOString();
  const to = new Date(Date.now() + DAY_MS).toISOString();

  const {
    data: events = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['time-events', employeeId],
    queryFn: () => listTimeEvents(employeeId, from, to),
    enabled: employeeId !== '',
  });

  const mutation = useMutation({
    mutationFn: (type: TimeEventType) =>
      recordTimeEvent(
        { employeeId, eventType: type, occurredAt: new Date().toISOString(), zoneId },
        crypto.randomUUID(),
      ),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['time-events', employeeId] }),
  });

  const punch = (type: TimeEventType) => {
    if (employeeId !== '') {
      mutation.mutate(type);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Time Clock
      </Typography>

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ alignItems: 'center', mb: 3, flexWrap: 'wrap' }}>
        <TextField
          select
          label="Employee"
          size="small"
          sx={{ minWidth: 220 }}
          value={employeeId}
          onChange={(event) => setEmployeeId(event.target.value)}
        >
          {employees.map((employee) => (
            <MenuItem key={employee.id} value={employee.id}>
              {employee.employeeNo} - {employee.firstName} {employee.lastName}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          label="Zone"
          size="small"
          value={zoneId}
          onChange={(event) => setZoneId(event.target.value)}
        />
        <Button variant="contained" color="success" disabled={!employeeId || mutation.isPending} onClick={() => punch('CLOCK_IN')}>
          Clock in
        </Button>
        <Button variant="contained" color="error" disabled={!employeeId || mutation.isPending} onClick={() => punch('CLOCK_OUT')}>
          Clock out
        </Button>
        <Button variant="outlined" disabled={!employeeId || mutation.isPending} onClick={() => punch('BREAK_START')}>
          Break start
        </Button>
        <Button variant="outlined" disabled={!employeeId || mutation.isPending} onClick={() => punch('BREAK_END')}>
          Break end
        </Button>
      </Stack>

      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {mutation.error.message}
        </Alert>
      )}

      <Typography variant="h6" gutterBottom>
        Timeline
      </Typography>
      {employeeId === '' ? (
        <Typography color="text.secondary">Select an employee to view their time events.</Typography>
      ) : isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load time events.</Alert>
      ) : (
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Type</TableCell>
              <TableCell>Occurred at</TableCell>
              <TableCell>Received at</TableCell>
              <TableCell>Source</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {events.map((event) => (
              <TableRow key={event.id}>
                <TableCell>{event.eventType}</TableCell>
                <TableCell>{formatInstant(event.occurredAt, event.zoneId)}</TableCell>
                <TableCell>{formatInstant(event.receivedAt, event.zoneId)}</TableCell>
                <TableCell>{event.source}</TableCell>
              </TableRow>
            ))}
            {events.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>No time events in the last 30 days.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}
    </Box>
  );
}
