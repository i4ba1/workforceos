import { Link, Outlet } from 'react-router-dom';
import {
  AppBar,
  Box,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
} from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import EventNoteIcon from '@mui/icons-material/EventNote';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import PaymentsIcon from '@mui/icons-material/Payments';
import PolicyIcon from '@mui/icons-material/Policy';

const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', icon: <DashboardIcon /> },
  { to: '/people', label: 'People', icon: <PeopleIcon /> },
  { to: '/schedule', label: 'Schedule', icon: <CalendarMonthIcon /> },
  { to: '/time', label: 'Time', icon: <AccessTimeIcon /> },
  { to: '/attendance', label: 'Attendance', icon: <EventNoteIcon /> },
  { to: '/approvals', label: 'Approvals', icon: <FactCheckIcon /> },
  { to: '/payroll', label: 'Payroll', icon: <PaymentsIcon /> },
  { to: '/policies', label: 'Policies', icon: <PolicyIcon /> },
];

const DRAWER_WIDTH = 240;

export function AppShell() {
  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (t) => t.zIndex.drawer + 1 }}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            WorkforceOS
          </Typography>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: DRAWER_WIDTH, boxSizing: 'border-box' },
        }}
      >
        <Toolbar />
        <Box sx={{ overflow: 'auto' }}>
          <List component="nav" aria-label="Primary">
            {NAV_ITEMS.map((item) => (
              <ListItemButton key={item.to} component={Link} to={item.to}>
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            ))}
          </List>
        </Box>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, mt: 8 }}>
        <Outlet />
      </Box>
    </Box>
  );
}

export default AppShell;
