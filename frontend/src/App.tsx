import { useCallback, useEffect, useState } from 'react'
import './App.css'

type UserRole = 'CUSTOMER' | 'MANAGER'
type GoogleProfile = { email: string; googleId: string }
type GoogleCredential = { credential: string }
type InventoryItem = { name: string; quantity: number; restockThreshold: number }

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (options: {
            client_id: string
            callback: (response: GoogleCredential) => void
          }) => void
          renderButton: (element: HTMLElement, options: Record<string, string>) => void
        }
      }
    }
  }
}

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
const backendUrl = import.meta.env.VITE_BACKEND_URL?.replace(/\/+$/, '')

function apiFetch(path: string, init?: RequestInit) {
  if (!backendUrl) {
    return Promise.reject(new Error('Set BACKEND_URL in the frontend environment to connect to the backend.'))
  }
  const url = import.meta.env.DEV ? `/api${path}` : `${backendUrl}${path}`
  return fetch(url, init)
}

function profileFromCredential(credential: string): GoogleProfile {
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

function App() {
  const [profile, setProfile] = useState<GoogleProfile | null>(null)
  const [role, setRole] = useState<UserRole>('CUSTOMER')
  const [screen, setScreen] = useState<'signin' | 'role' | 'customer' | 'manager'>('signin')
  const [items, setItems] = useState<InventoryItem[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleCredential = useCallback(async ({ credential }: GoogleCredential) => {
    setLoading(true)
    setError('')
    try {
      const nextProfile = profileFromCredential(credential)
      setProfile(nextProfile)
      const response = await apiFetch('/auth/role/email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: nextProfile.email }),
      })

      if (response.ok) {
        const savedRole = await readRole(response)
        setRole(savedRole)
        setScreen(savedRole === 'MANAGER' ? 'manager' : 'customer')
      } else if (response.status === 404) {
        setScreen('role')
      } else {
        const detail = (await response.text()).trim().slice(0, 220)
        const message = detail
          ? `Role lookup failed (HTTP ${response.status}): ${detail}`
          : `Role lookup failed (HTTP ${response.status} ${response.statusText}). Check that the backend and database are running.`
        throw new Error(message)
      }
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Sign-in could not be completed.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    if (screen !== 'signin' || !clientId) return

    const renderGoogleButton = () => {
      const mount = document.getElementById('google-button')
      if (!mount || !window.google) return false
      mount.replaceChildren()
      window.google.accounts.id.initialize({ client_id: clientId, callback: handleCredential })
      window.google.accounts.id.renderButton(mount, {
        theme: 'outline', size: 'large', shape: 'rectangular', text: 'continue_with', width: '320',
      })
      return true
    }

    if (renderGoogleButton()) return
    const existing = document.querySelector<HTMLScriptElement>('script[data-google-identity]')
    const script = existing ?? document.createElement('script')
    if (!existing) {
      script.src = 'https://accounts.google.com/gsi/client'
      script.async = true
      script.defer = true
      script.dataset.googleIdentity = 'true'
      document.head.append(script)
    }
    script.addEventListener('load', renderGoogleButton)
    return () => script.removeEventListener('load', renderGoogleButton)
  }, [handleCredential, screen])

  useEffect(() => {
    if (screen !== 'manager') return
    let active = true
    setLoading(true)
    setError('')
    apiFetch('/manager/ingredients', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ location: 'UTENSILS' }),
    })
      .then(async (response) => {
        if (!response.ok) throw new Error('Inventory could not be loaded. Check that the backend is running.')
        return response.json() as Promise<InventoryItem[]>
      })
      .then((data) => { if (active) setItems(data) })
      .catch((cause: unknown) => {
        if (active) setError(cause instanceof Error ? cause.message : 'Inventory could not be loaded.')
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [screen])

  const chooseRole = async (selectedRole: UserRole) => {
    if (!profile) return
    setLoading(true)
    setError('')
    try {
      const response = await apiFetch('/auth/newuser', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ...profile, role: selectedRole }),
      })
      if (!response.ok) {
        const detail = await response.text()
        throw new Error(detail || 'Your role could not be saved.')
      }
      setRole(selectedRole)
      setScreen(selectedRole === 'MANAGER' ? 'manager' : 'customer')
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Your role could not be saved.')
    } finally {
      setLoading(false)
    }
  }

  const signOut = () => {
    setProfile(null)
    setScreen('signin')
    setError('')
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#home" onClick={(event) => { event.preventDefault(); signOut() }} aria-label="Panda Express home">
          <span className="brand-wordmark"><strong>PANDA</strong><b>EXPRESS</b></span>
        </a>
        <span className="topbar-note">STORE OPERATIONS</span>
        {profile && <button className="signout" type="button" onClick={signOut}>Sign out</button>}
      </header>

      {screen === 'signin' && (
        <main className="auth-layout">
          <section className="welcome-panel">
            <div>
              <div className="eyebrow">Panda Express</div>
              <h1>We Wok<br />For You.</h1>
              <p>Sign in to order your favorites or keep your restaurant running smoothly.</p>
            </div>
            <div className="bowl-art" aria-hidden="true"><span>〰</span><div /></div>
            <small>Freshly prepared. Always ready to serve.</small>
          </section>
          <section className="auth-panel">
            <div className="step-indicator" aria-hidden="true"><i className="active" /><i /><i /></div>
            <h2>Welcome to Panda Express</h2>
            <p className="muted">Sign in with Google to get started.</p>
            {clientId ? <div id="google-button" className="google-mount" /> : (
              <div className="setup-notice" role="status">
                Google sign-in needs a client ID. Set <code>VITE_GOOGLE_CLIENT_ID</code> in the frontend environment.
              </div>
            )}
            {loading && <p className="muted" role="status">Checking your account…</p>}
            {error && <p className="error-message" role="alert">{error}</p>}
            <p className="auth-foot">Secure sign in · Your account, your role</p>
          </section>
        </main>
      )}

      {screen === 'role' && (
        <main className="role-layout">
          <div className="role-content">
            <div className="step-indicator" aria-hidden="true"><i className="active" /><i className="active" /><i /></div>
            <div className="eyebrow orange">One quick step</div>
            <h1>How will you use<br />Panda Express?</h1>
            <p className="muted">Choose a role for <strong>{profile?.email}</strong>. This is saved to your account.</p>
            <div className="role-options">
              <button className="role-option" type="button" onClick={() => void chooseRole('CUSTOMER')} disabled={loading}>
                <span className="role-symbol" aria-hidden="true">◉</span><strong>Customer</strong><span>Browse menu and place orders</span>
              </button>
              <button className="role-option" type="button" onClick={() => void chooseRole('MANAGER')} disabled={loading}>
                <span className="role-symbol" aria-hidden="true">▦</span><strong>Manager</strong><span>Track stock and restock needs</span>
              </button>
            </div>
            {loading && <p className="muted" role="status">Saving your role…</p>}
            {error && <p className="error-message" role="alert">{error}</p>}
          </div>
        </main>
      )}

      {screen === 'customer' && (
        <main className="construction-layout">
          <section className="construction-card">
            <div className="construction-icon" aria-hidden="true">🥡</div>
            <div className="eyebrow orange">Coming soon</div>
            <h1>Your Panda Express<br />experience is coming soon.</h1>
            <p className="muted">Online ordering is under construction. We’re getting your favorite Panda Express experience ready.</p>
            <div className="construction-rule"><span /></div>
            <p className="signed-in-as">Signed in as <strong>{profile?.email}</strong></p>
          </section>
        </main>
      )}

      {screen === 'manager' && (
        <main className="manager-page">
          <div className="manager-heading">
            <div><div className="eyebrow">Panda Express · Store operations</div><h1>Inventory</h1><p className="muted">Keep every wok ready for the next order.</p></div>
            <div className="manager-user"><span className="avatar">{profile?.email.slice(0, 1).toUpperCase()}</span><span>{profile?.email}</span></div>
          </div>
          <section className="inventory-section">
            <div className="inventory-header"><div><h2>Utensils &amp; supplies</h2><p className="muted">Current quantity compared with its restock threshold.</p></div></div>
            {loading && <p className="muted" role="status">Loading inventory…</p>}
            {error && <p className="error-message" role="alert">{error}</p>}
            {!loading && !error && <div className="table-wrap">
              <table>
                <thead><tr><th>Inventory item</th><th>Amount on hand</th><th>Restock threshold</th><th>Status</th></tr></thead>
                <tbody>{items.map((item) => {
                  const low = item.quantity <= item.restockThreshold
                  return <tr key={item.name}>
                    <td className="item-name">{item.name}</td>
                    <td className="amount">{item.quantity} <span>ea</span></td>
                    <td className="amount">{item.restockThreshold} <span>ea</span></td>
                    <td><span className={`status ${low ? 'status-low' : 'status-ok'}`}>{low ? 'Restock' : 'In stock'}</span></td>
                  </tr>
                })}</tbody>
              </table>
              {items.length === 0 && <p className="empty-state">No inventory items found.</p>}
            </div>}
          </section>
          <p className="manager-foot">Signed in as {role === 'MANAGER' ? 'manager' : 'customer'}</p>
        </main>
      )}
    </div>
  )
}

export default App
