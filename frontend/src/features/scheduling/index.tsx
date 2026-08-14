import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
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
import { createScheduleEntry, createShiftTemplate, listShiftTemplates } from './api';
import { listEmployees } from '../people/api';
import { datetimeLocalToInstant } from '../../shared/lib/format';

const shiftTemplateSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  localStart: z.string().min(1, 'Start time is required'),
  localEnd: z.string().min(1, 'End time is required'),
  zoneId: z.string().min(1, 'Zone is required'),
  breakMinutes: z.string().regex(/^\d+$/, 'Enter a number'),
});

const scheduleEntrySchema = z.object({
  employeeId: z.string().uuid('Select an employee'),
  businessDate: z.string().min(1, 'Business date is required'),
  zoneId: z.string().min(1, 'Zone is required'),
  plannedStart: z.string().min(1, 'Start is required'),
  plannedEnd: z.string().min(1, 'End is required'),
});

type ShiftTemplateValues = z.infer<typeof shiftTemplateSchema>;
type ScheduleEntryValues = z.infer<typeof scheduleEntrySchema>;

export default function SchedulingPage() {
  const queryClient = useQueryClient();

  const { data: employees = [] } = useQuery({ queryKey: ['employees'], queryFn: listEmployees });
  const { data: templates = [], isLoading, isError } = useQuery({
    queryKey: ['shift-templates'],
    queryFn: listShiftTemplates,
  });

  const templateMutation = useMutation({
    mutationFn: createShiftTemplate,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shift-templates'] });
      templateForm.reset();
    },
  });

  const entryMutation = useMutation({
    mutationFn: createScheduleEntry,
    onSuccess: () => entryForm.reset(),
  });

  const templateForm = useForm<ShiftTemplateValues>({
    resolver: zodResolver(shiftTemplateSchema),
    defaultValues: { name: '', localStart: '08:00', localEnd: '17:00', zoneId: 'Asia/Jakarta', breakMinutes: '0' },
  });

  const entryForm = useForm<ScheduleEntryValues>({
    resolver: zodResolver(scheduleEntrySchema),
    defaultValues: { employeeId: '', businessDate: '', zoneId: 'Asia/Jakarta', plannedStart: '', plannedEnd: '' },
  });

  const onSubmitTemplate = (values: ShiftTemplateValues) => {
    templateMutation.mutate({
      name: values.name,
      localStart: values.localStart,
      localEnd: values.localEnd,
      zoneId: values.zoneId,
      breakMinutes: Number(values.breakMinutes),
    });
  };

  const onSubmitEntry = (values: ScheduleEntryValues) => {
    entryMutation.mutate({
      employeeId: values.employeeId,
      businessDate: values.businessDate,
      zoneId: values.zoneId,
      plannedStart: datetimeLocalToInstant(values.plannedStart),
      plannedEnd: datetimeLocalToInstant(values.plannedEnd),
    });
  };

  const templateErrors = templateForm.formState.errors;
  const entryErrors = entryForm.formState.errors;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Schedule
      </Typography>

      <Box component="form" onSubmit={templateForm.handleSubmit(onSubmitTemplate)} sx={{ mb: 4 }} noValidate>
        <Typography variant="h6" gutterBottom>
          Add shift template
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ alignItems: 'flex-start', flexWrap: 'wrap' }}>
          <TextField label="Name" size="small" error={!!templateErrors.name} helperText={templateErrors.name?.message} {...templateForm.register('name')} />
          <TextField label="Start" size="small" type="time" error={!!templateErrors.localStart} helperText={templateErrors.localStart?.message} {...templateForm.register('localStart')} />
          <TextField label="End" size="small" type="time" error={!!templateErrors.localEnd} helperText={templateErrors.localEnd?.message} {...templateForm.register('localEnd')} />
          <TextField label="Zone" size="small" error={!!templateErrors.zoneId} helperText={templateErrors.zoneId?.message} {...templateForm.register('zoneId')} />
          <TextField label="Break (min)" size="small" type="number" error={!!templateErrors.breakMinutes} helperText={templateErrors.breakMinutes?.message} {...templateForm.register('breakMinutes')} />
          <Button type="submit" variant="contained" disabled={templateMutation.isPending}>
            Add template
          </Button>
        </Stack>
        {templateMutation.isError && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {templateMutation.error.message}
          </Alert>
        )}
      </Box>

      <Typography variant="h6" gutterBottom>
        Shift templates
      </Typography>
      {isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load shift templates.</Alert>
      ) : (
        <Table size="small" sx={{ mb: 4 }}>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Window</TableCell>
              <TableCell>Zone</TableCell>
              <TableCell>Break</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {templates.map((template) => (
              <TableRow key={template.id}>
                <TableCell>{template.name}</TableCell>
                <TableCell>
                  {template.localStart} - {template.localEnd}
                </TableCell>
                <TableCell>{template.zoneId}</TableCell>
                <TableCell>{template.breakMinutes}m</TableCell>
              </TableRow>
            ))}
            {templates.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>No shift templates yet.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}

      <Box component="form" onSubmit={entryForm.handleSubmit(onSubmitEntry)} noValidate>
        <Typography variant="h6" gutterBottom>
          Add schedule entry
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ alignItems: 'flex-start', flexWrap: 'wrap' }}>
          <TextField select label="Employee" size="small" sx={{ minWidth: 200 }} error={!!entryErrors.employeeId} helperText={entryErrors.employeeId?.message} {...entryForm.register('employeeId')}>
            {employees.map((employee) => (
              <MenuItem key={employee.id} value={employee.id}>
                {employee.employeeNo} - {employee.firstName} {employee.lastName}
              </MenuItem>
            ))}
          </TextField>
          <TextField label="Business date" size="small" type="date" error={!!entryErrors.businessDate} helperText={entryErrors.businessDate?.message} {...entryForm.register('businessDate')} />
          <TextField label="Zone" size="small" error={!!entryErrors.zoneId} helperText={entryErrors.zoneId?.message} {...entryForm.register('zoneId')} />
          <TextField label="Start (UTC)" size="small" type="datetime-local" error={!!entryErrors.plannedStart} helperText={entryErrors.plannedStart?.message} {...entryForm.register('plannedStart')} />
          <TextField label="End (UTC)" size="small" type="datetime-local" error={!!entryErrors.plannedEnd} helperText={entryErrors.plannedEnd?.message} {...entryForm.register('plannedEnd')} />
          <Button type="submit" variant="contained" disabled={entryMutation.isPending}>
            Add entry
          </Button>
        </Stack>
        {entryMutation.isError && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {entryMutation.error.message}
          </Alert>
        )}
        {entryMutation.isSuccess && (
          <Alert severity="success" sx={{ mt: 2 }}>
            Schedule entry created.
          </Alert>
        )}
      </Box>
    </Box>
  );
}
