import { Box, Typography } from '@mui/material';

interface PagePlaceholderProps {
  title: string;
  description?: string;
}

/** Temporary screen body for scaffolded feature areas. */
export function PagePlaceholder({ title, description }: PagePlaceholderProps) {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {title}
      </Typography>
      <Typography variant="body1" color="text.secondary">
        {description ?? 'This area is scaffolded and will be implemented in a later phase.'}
      </Typography>
    </Box>
  );
}
