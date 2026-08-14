import { createTheme } from '@mui/material/styles';

/** Tenant-neutral design tokens; business zone is never implied by the theme. */
export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1a4f8b' },
    secondary: { main: '#2e7d32' },
  },
  shape: { borderRadius: 6 },
  typography: {
    fontFamily:
      '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
  },
});
