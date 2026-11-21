import { apiFetch } from './client'
import type { GoogleProfile, UserRole } from '../types'

export function profileFromCredential(credential: string): GoogleProfile {
  const payload = credential.split('.')[1]
  if (!payload) throw new Error('Google returned an invalid sign-in response.')

  const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
  const decoded = JSON.parse(window.atob(normalized)) as { email?: string; sub?: string }
  if (!decoded.email || !decoded.sub) throw new Error('Google did not provide an email and account ID.')
  return { email: decoded.email, googleId: decoded.sub }
}

async function readRole(response: Response): Promise<UserRole> {
  const value = await response.json() as string | { role?: string; displayName?: string }
  const role = typeof value === 'string' ? value : value.role ?? value.displayName
  if (role?.toUpperCase() !== 'CUSTOMER' && role?.toUpperCase() !== 'MANAGER') {
    throw new Error('The account returned an unknown role.')
  }
  return role.toUpperCase() as UserRole
}

export async function lookupRole(email: string): Promise<UserRole | null> {
  const response = await apiFetch('/auth/role/email', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  })
  if (response.ok) return readRole(response)
  if (response.status === 404) return null

  const detail = (await response.text()).trim().slice(0, 220)
  const message = detail
    ? `Role lookup failed (HTTP ${response.status}): ${detail}`
    : `Role lookup failed (HTTP ${response.status} ${response.statusText}). Check that the backend and database are running.`
  throw new Error(message)
}

export async function createUser(profile: GoogleProfile, role: UserRole) {
  const response = await apiFetch('/auth/newuser', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ ...profile, role }),
  })
  if (!response.ok) {
    const detail = await response.text()
    throw new Error(detail || 'Your role could not be saved.')
  }
}
