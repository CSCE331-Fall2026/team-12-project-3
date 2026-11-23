const backendUrl = import.meta.env.VITE_BACKEND_URL?.replace(/\/+$/, '')

export function apiFetch(path: string, init?: RequestInit) {
  if (!backendUrl) {
    return Promise.reject(new Error('Set BACKEND_URL in the frontend environment to connect to the backend.'))
  }
  const url = import.meta.env.DEV ? `/api${path}` : `${backendUrl}${path}`
  return fetch(url, init)
}
