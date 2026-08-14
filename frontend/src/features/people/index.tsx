import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { createEmployee, listEmployees } from './api';

const schema = z.object({
  employeeNo: z.string().min(1, 'Employee number is required'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.union([z.string().email('Invalid email'), z.literal('')]),
});

type FormValues = z.infer<typeof schema>;

export default function PeoplePage() {
  const queryClient = useQueryClient();
  const { data: employees = [], isLoading, isError } = useQuery({
    queryKey: ['employees'],
    queryFn: listEmployees,
  });

  const mutation = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['employees'] });
      reset();
    },
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { employeeNo: '', firstName: '', lastName: '', email: '' },
  });

  const onSubmit = (values: FormValues) => {
    mutation.mutate({
      employeeNo: values.employeeNo,
      firstName: values.firstName,
      lastName: values.lastName,
      email: values.email === '' ? undefined : values.email,
    });
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        People
      </Typography>

      <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mb: 4 }} noValidate>
        <Typography variant="h6" gutterBottom>
          Add employee
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ alignItems: 'flex-start' }}>
          <TextField
            label="Employee No"
            size="small"
            error={!!errors.employeeNo}
            helperText={errors.employeeNo?.message}
            {...register('employeeNo')}
          />
          <TextField
            label="First name"
            size="small"
            error={!!errors.firstName}
            helperText={errors.firstName?.message}
            {...register('firstName')}
          />
          <TextField
            label="Last name"
            size="small"
            error={!!errors.lastName}
            helperText={errors.lastName?.message}
            {...register('lastName')}
          />
          <TextField
            label="Email"
            size="small"
            error={!!errors.email}
            helperText={errors.email?.message}
            {...register('email')}
          />
          <Button type="submit" variant="contained" disabled={mutation.isPending}>
            Add
          </Button>
        </Stack>
        {mutation.isError && (
          <Alert severity="error" sx={{ mt: 2 }}>
            {mutation.error.message}
          </Alert>
        )}
      </Box>

      <Typography variant="h6" gutterBottom>
        Employees
      </Typography>
      {isLoading ? (
        <CircularProgress />
      ) : isError ? (
        <Alert severity="error">Failed to load employees.</Alert>
      ) : (
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Employee No</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {employees.map((employee) => (
              <TableRow key={employee.id}>
                <TableCell>{employee.employeeNo}</TableCell>
                <TableCell>
                  {employee.firstName} {employee.lastName}
                </TableCell>
                <TableCell>{employee.email ?? '-'}</TableCell>
                <TableCell>{employee.status}</TableCell>
              </TableRow>
            ))}
            {employees.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>No employees yet.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      )}
    </Box>
  );
}
