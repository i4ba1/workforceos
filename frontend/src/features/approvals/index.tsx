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
import { approveCase, listApprovalCases, rejectCase, type ApprovalCase } from './api';

type Decision = { kind: 'approve' | 'reject'; case: ApprovalCase };

export default function ApprovalsPage() {
  const queryClient = useQueryClient();
  const { data: cases = [], isLoading, isError } = useQuery({
    queryKey: ['approval-cases'],
    queryFn: listApprovalCases,
  });

  const [decision, setDecision] = useState<Decision | null>(null);
  const [reason, setReason] = useState('');

  const mutation = useMutation({
    mutationFn: ({ d, reason }: { d: Decision; reason: string }) =>
      d.kind === 'approve'
        ? approveCase(d.case.id, d.case.version, reason)
        : rejectCase(d.case.id, d.case.version, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['approval-cases'] });
      setDecision(null);
      setReason('');
    },
  });

  const confirm = () => {
    if (decision && reason.trim()) {
      mutation.mutate({ d: decision, reason: reason.trim() });
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Approvals
      </Typography>

      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {mutation.error.message}
        </Alert>
      )}

      {isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load approval cases.</Alert>
      ) : (
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Subject</TableCell>
              <TableCell>Subject ID</TableCell>
              <TableCell>Reason</TableCell>
              <TableCell>Version</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {cases.map((c) => (
              <TableRow key={c.id}>
                <TableCell>{c.subjectType}</TableCell>
                <TableCell>{c.subjectId}</TableCell>
                <TableCell>{c.reason}</TableCell>
                <TableCell>{c.version}</TableCell>
                <TableCell align="right">
                  <Button
                    size="small"
                    color="success"
                    onClick={() => setDecision({ kind: 'approve', case: c })}
                  >
                    Approve
                  </Button>
                  <Button
                    size="small"
                    color="error"
                    onClick={() => setDecision({ kind: 'reject', case: c })}
                  >
                    Reject
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {cases.length === 0 && (
              <TableRow>
                <TableCell colSpan={5}>No open approval cases.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}

      <Dialog open={decision !== null} onClose={() => setDecision(null)} fullWidth maxWidth="sm">
        <DialogTitle>
          {decision?.kind === 'approve' ? 'Approve' : 'Reject'} case (v{decision?.case.version})
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            {decision?.case.subjectType} {decision?.case.subjectId}
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
          <Button onClick={() => setDecision(null)}>Cancel</Button>
          <Button
            onClick={confirm}
            color={decision?.kind === 'approve' ? 'success' : 'error'}
            disabled={!reason.trim() || mutation.isPending}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
