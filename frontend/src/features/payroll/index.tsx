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
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import {
  closePeriod,
  exportPeriod,
  getReadiness,
  listExports,
  listPayPeriods,
  openPayPeriod,
  reopenPeriod,
  type PayPeriod,
} from './api';
import { formatMinutes } from '../../shared/lib/format';

export default function PayrollPage() {
  const queryClient = useQueryClient();
  const { data: periods = [], isLoading, isError } = useQuery({
    queryKey: ['pay-periods'],
    queryFn: listPayPeriods,
  });

  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [reopenFor, setReopenFor] = useState<PayPeriod | null>(null);
  const [reopenReason, setReopenReason] = useState('');

  const readiness = useQuery({
    queryKey: ['readiness', selectedId],
    queryFn: () => getReadiness(selectedId as string),
    enabled: selectedId !== null,
  });
  const exports = useQuery({
    queryKey: ['payroll-exports', selectedId],
    queryFn: () => listExports(selectedId as string),
    enabled: selectedId !== null,
  });

  const openMutation = useMutation({
    mutationFn: () => openPayPeriod(startDate, endDate),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pay-periods'] });
      setStartDate('');
      setEndDate('');
    },
  });

  const closeMutation = useMutation({
    mutationFn: (id: string) => closePeriod(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pay-periods'] });
      queryClient.invalidateQueries({ queryKey: ['readiness', selectedId] });
    },
  });

  const reopenMutation = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) => reopenPeriod(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pay-periods'] });
      setReopenFor(null);
      setReopenReason('');
    },
  });

  const exportMutation = useMutation({
    mutationFn: (id: string) => exportPeriod(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['payroll-exports', selectedId] }),
  });

  const selected = periods.find((p) => p.id === selectedId) ?? null;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Payroll
      </Typography>

      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ alignItems: 'flex-start', mb: 3 }}>
        <TextField
          label="Start date"
          type="date"
          size="small"
          value={startDate}
          onChange={(event) => setStartDate(event.target.value)}
          slotProps={{ htmlInput: { shrink: true } }}
        />
        <TextField
          label="End date"
          type="date"
          size="small"
          value={endDate}
          onChange={(event) => setEndDate(event.target.value)}
          slotProps={{ htmlInput: { shrink: true } }}
        />
        <Button
          variant="contained"
          disabled={!startDate || !endDate || openMutation.isPending}
          onClick={() => openMutation.mutate()}
        >
          Open period
        </Button>
      </Stack>

      {openMutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {openMutation.error.message}
        </Alert>
      )}
      {closeMutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {closeMutation.error.message}
        </Alert>
      )}

      {isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load pay periods.</Alert>
      ) : (
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Range</TableCell>
              <TableCell>State</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {periods.map((period) => (
              <TableRow key={period.id} selected={period.id === selectedId} hover onClick={() => setSelectedId(period.id)}>
                <TableCell>
                  {period.startDate} → {period.endDate}
                </TableCell>
                <TableCell>{period.state}</TableCell>
                <TableCell>{period.version}</TableCell>
                <TableCell align="right" onClick={(event) => event.stopPropagation()}>
                  <Button size="small" disabled={period.state !== 'CLOSED'} onClick={() => setReopenFor(period)}>
                    Reopen
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {periods.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>No pay periods yet.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}

      {selected && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Period {selected.startDate} → {selected.endDate}
          </Typography>
          {readiness.data && (
            <Typography variant="body1" sx={{ mb: 2 }}>
              Readiness: {readiness.data.totalEmployees} employees · {readiness.data.unresolvedCount} unresolved ·{' '}
              {formatMinutes(readiness.data.totalRegularMinutes)} regular ·{' '}
              {formatMinutes(readiness.data.totalOvertimeMinutes)} overtime ·{' '}
              {readiness.data.finalizedPercent.toFixed(1)}% finalized
            </Typography>
          )}
          <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
            <Button
              variant="contained"
              disabled={selected.state !== 'OPEN' && selected.state !== 'REOPENED'}
              onClick={() => closeMutation.mutate(selected.id)}
            >
              Close
            </Button>
            <Button
              variant="outlined"
              disabled={selected.state !== 'CLOSED'}
              onClick={() => exportMutation.mutate(selected.id)}
            >
              Export CSV
            </Button>
          </Stack>
          {exports.data && exports.data.length > 0 && (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Version</TableCell>
                  <TableCell>Checksum</TableCell>
                  <TableCell>Generated at</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {exports.data.map((e) => (
                  <TableRow key={e.id}>
                    <TableCell>{e.version}</TableCell>
                    <TableCell>{e.checksum}</TableCell>
                    <TableCell>{e.generatedAt}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </Box>
      )}

      <Dialog open={reopenFor !== null} onClose={() => setReopenFor(null)} fullWidth maxWidth="sm">
        <DialogTitle>Reopen period</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Reopening a closed period requires a reason and creates a new export version on re-close.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            label="Reason"
            fullWidth
            value={reopenReason}
            onChange={(event) => setReopenReason(event.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReopenFor(null)}>Cancel</Button>
          <Button
            onClick={() => reopenFor && reopenMutation.mutate({ id: reopenFor.id, reason: reopenReason.trim() })}
            disabled={!reopenReason.trim() || reopenMutation.isPending}
          >
            Reopen
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
