# Deployment — separate frontend & backend

WorkforceOS ships two independent artifacts that are deployed separately:

| Artifact | Build | Runtime |
|----------|-------|---------|
| **Backend** (REST API) | Spring Boot `.jar` or OCI image | JVM / container on `:8080` |
| **Frontend** (SPA) | Static files (`frontend/dist/`) | Nginx, CDN, S3, Netlify, ... |

The frontend calls the backend over HTTP; they do **not** need to share a host, but the
backend must allow the frontend's origin via CORS.

---

## 1. Backend

### Build

```bash
cd backend
./mvnw -DskipTests package      # -> target/workforceos-0.1.0-SNAPSHOT.jar
```

### Run

```bash
java -jar target/workforceos-0.1.0-SNAPSHOT.jar
```

### Or build & run the container

```bash
docker build -t workforceos-backend backend
docker run -e DB_URL=jdbc:postgresql://<db-host>:5432/workforceos \
           -e DB_USERNAME=postgres -e DB_PASSWORD=root \
           -e CORS_ALLOWED_ORIGINS=https://app.example.com \
           -p 8080:8080 workforceos-backend
```

### Backend environment variables

| Variable | Default | Purpose |
|----------|---------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/workforceos` | JDBC URL |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | `root` | DB password |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated frontend origins |
| `SERVER_PORT` | `8080` | Listen port |
| `SPRING_PROFILES_ACTIVE` | `local` | `local` (dev identity) vs `prod` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | local OTLP | Tracing endpoint |

> Production should set `SPRING_PROFILES_ACTIVE=prod` and wire an OIDC resource server
> (replace the dev-mode `X-Tenant-Id` identity filter). See ADR-0006.

---

## 2. Frontend

The backend URL is baked in at build time via `VITE_API_BASE_URL`.

```bash
cd frontend
npm ci
VITE_API_BASE_URL=https://api.example.com/api/v1 npm run build   # -> dist/
```

Two common patterns:

- **Absolute API URL** — set `VITE_API_BASE_URL` to the backend origin (CORS required).
- **Same-origin via reverse proxy** — leave it empty (defaults to `/api/v1`) and proxy
  `/api` to the backend from your web server.

### Serve the static output

Deploy `dist/` to any static host (Nginx, CDN, S3 + CloudFront, Netlify, GitHub Pages).
An example Nginx config is provided in `frontend/nginx.conf` (SPA fallback for deep links).

### Or build & run the frontend container

```bash
docker build --build-arg VITE_API_BASE_URL=https://api.example.com/api/v1 \
              -t workforceos-frontend frontend
docker run -p 80:80 workforceos-frontend
```

---

## 3. CORS

Add every frontend origin to the backend's allow-list:

```bash
CORS_ALLOWED_ORIGINS=https://app.example.com,https://admin.example.com
```

---

## 4. Release pipeline

On a `v*` tag, `.github/workflows/release.yml` builds both OCI images and publishes them
to GitHub Container Registry (`ghcr.io/<owner>/workforceos-backend`,
`ghcr.io/<owner>/workforceos-frontend`).

```bash
git tag v0.1.0 && git push origin v0.1.0
```
