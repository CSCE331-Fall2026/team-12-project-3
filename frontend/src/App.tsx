import { useState } from 'react'

import AppHeader from './components/AppHeader'
import { useGoogleSignIn } from './hooks/useGoogleSignIn'
import CustomerScreen from './screens/CustomerScreen'
import ManagerScreen from './screens/ManagerScreen'
import RoleSelectionScreen from './screens/RoleSelectionScreen'
import SignInScreen from './screens/SignInScreen'
import type { AppScreen } from './types'
import './App.css'

type UserRole = 'CUSTOMER' | 'MANAGER'
type GoogleProfile = { email: string; googleId: string }
type GoogleCredential = { credential: string }
type InventoryCategory = 'FREEZER' | 'REFRIGERATOR' | 'PANTRY' | 'UTENSILS'
type InventoryItem = { name: string; quantity?: number; restockThreshold: number; unit?: string }
type IngredientForecast = {
  name: string
  averageDailyUsage: number
  daysUntilThreshold: number | null
}

const inventoryCategories: { id: InventoryCategory; label: string }[] = [
  { id: 'FREEZER', label: 'Freezer' },
  { id: 'REFRIGERATOR', label: 'Refrigerator' },
  { id: 'PANTRY', label: 'Pantry' },
  { id: 'UTENSILS', label: 'Utensils' },
]

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

async function fetchInventoryItems(category: InventoryCategory) {
  const response = await apiFetch('/manager/ingredients', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ location: category }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Inventory could not be loaded. Check that the backend is running.')
  }
  return response.json() as Promise<InventoryItem[]>
}

async function fetchIngredientForecasts(category: Exclude<InventoryCategory, 'UTENSILS'>) {
  const response = await apiFetch('/manager/ingredient-forecasts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ location: category }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Ingredient forecasts could not be loaded.')
  }
  return response.json() as Promise<IngredientForecast[]>
}

function displayUnit(unit: string | undefined, category: InventoryCategory) {
  if (category === 'UTENSILS') return 'ea'
  switch (unit?.toUpperCase()) {
    case 'KILOGRAMS': return 'kg'
    case 'LITERS': return 'L'
    case 'ITEMS': return 'ea'
    default: return unit ?? ''
  }
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
  const [screen, setScreen] = useState<'signin' | 'role' | 'customer' | 'manager'>('signin')
  const [items, setItems] = useState<InventoryItem[]>([])
  const [forecasts, setForecasts] = useState<Record<string, IngredientForecast>>({})
  const [category, setCategory] = useState<InventoryCategory>('FREEZER')
  const [editingItem, setEditingItem] = useState<string | null>(null)
  const [restockQuantity, setRestockQuantity] = useState('')
  const [restocking, setRestocking] = useState(false)
  const [loading, setLoading] = useState(false)
  const [inventoryLoading, setInventoryLoading] = useState(false)
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
        if (savedRole === 'MANAGER') {
          setItems([])
          setForecasts({})
          setInventoryLoading(true)
        }
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
    Promise.all([
      fetchInventoryItems(category),
      category === 'UTENSILS' ? Promise.resolve([]) : fetchIngredientForecasts(category),
    ])
      .then(([data, forecastData]) => {
        if (!active) return
        setItems(data)
        setForecasts(Object.fromEntries(forecastData.map((forecast) => [forecast.name, forecast])))
      })
      .catch((cause: unknown) => {
        if (active) setError(cause instanceof Error ? cause.message : 'Inventory could not be loaded.')
      })
      .finally(() => { if (active) setInventoryLoading(false) })
    return () => { active = false }
  }, [screen, category])

  const saveRestock = async (item: InventoryItem) => {
    const quantity = Number(restockQuantity)
    if (!Number.isInteger(quantity) || quantity <= 0) {
      setError('Enter a whole number greater than zero.')
      return
    }

    setRestocking(true)
    setError('')
    try {
      const response = await apiFetch('/manager/restock/', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: item.name, quantity, isUtensil: category === 'UTENSILS' }),
      })
      if (!response.ok) {
        const detail = (await response.text()).trim()
        throw new Error(detail || 'The item could not be restocked.')
      }
      const [updatedItems, updatedForecasts] = await Promise.all([
        fetchInventoryItems(category),
        category === 'UTENSILS' ? Promise.resolve([]) : fetchIngredientForecasts(category),
      ])
      setItems(updatedItems)
      setForecasts(Object.fromEntries(updatedForecasts.map((forecast) => [forecast.name, forecast])))
      setEditingItem(null)
      setRestockQuantity('')
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'The item could not be restocked.')
    } finally {
      setRestocking(false)
    }
  }

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
      if (selectedRole === 'MANAGER') {
        setItems([])
        setForecasts({})
        setInventoryLoading(true)
      }
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
function App() {
  const [screen, setScreen] = useState<AppScreen>('signin')
  const auth = useGoogleSignIn(screen, setScreen)

  return (
    <div className="app-shell">
      <AppHeader signedIn={auth.profile !== null} onSignOut={auth.signOut} />
      {screen === 'signin' && (
        <SignInScreen clientId={auth.clientId} loading={auth.loading} error={auth.error} />
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
            <div className="inventory-header"><div><h2>{inventoryCategories.find((entry) => entry.id === category)?.label} inventory</h2><p className="muted">Current quantity compared with its restock threshold.</p></div></div>
            <nav className="category-tabs" aria-label="Inventory categories">
              {inventoryCategories.map((entry) => (
                <button
                  key={entry.id}
                  className={`category-tab ${category === entry.id ? 'category-tab-active' : ''}`}
                  type="button"
                  aria-pressed={category === entry.id}
                  onClick={() => {
                    setCategory(entry.id)
                    setItems([])
                    setForecasts({})
                    setInventoryLoading(true)
                    setEditingItem(null)
                    setRestockQuantity('')
                    setError('')
                  }}
                >
                  {entry.label}
                </button>
              ))}
            </nav>
            {inventoryLoading && <p className="muted" role="status">Loading inventory…</p>}
            {error && <p className="error-message" role="alert">{error}</p>}
            {!inventoryLoading && <div className="table-wrap">
              <table>
                <thead><tr><th>Inventory item</th><th>Amount on hand</th><th>Restock threshold</th><th>Status</th><th>Restock forecast</th></tr></thead>
                <tbody>{items.map((item) => {
                  const unit = displayUnit(item.unit, category)
                  const low = item.quantity !== undefined && item.quantity <= item.restockThreshold
                  const forecast = forecasts[item.name]
                  return <tr key={item.name}>
                    <td className="item-name">{item.name}</td>
                    <td>
                      <div className="quantity-cell">
                        <span className="amount">{item.quantity ?? '—'} {unit && <span>{unit}</span>}</span>
                        {editingItem === item.name ? (
                          <form className="restock-form" onSubmit={(event) => { event.preventDefault(); void saveRestock(item) }}>
                            <label className="sr-only" htmlFor={`restock-${category}-${item.name}`}>Amount to add to {item.name}</label>
                            <input
                              id={`restock-${category}-${item.name}`}
                              type="number"
                              min="1"
                              step="1"
                              value={restockQuantity}
                              onChange={(event) => setRestockQuantity(event.target.value)}
                              placeholder="Qty"
                              required
                            />
                            <button className="small-action save-action" type="submit" disabled={restocking}>Save</button>
                            <button className="small-action cancel-action" type="button" disabled={restocking} onClick={() => { setEditingItem(null); setRestockQuantity(''); setError('') }}>Cancel</button>
                          </form>
                        ) : (
                          <button className="edit-action" type="button" onClick={() => { setEditingItem(item.name); setRestockQuantity(''); setError('') }}>Edit</button>
                        )}
                      </div>
                    </td>
                    <td className="amount">{item.restockThreshold} {unit && <span>{unit}</span>}</td>
                    <td><span className={`status ${item.quantity === undefined ? 'status-unavailable' : low ? 'status-low' : 'status-ok'}`}>{item.quantity === undefined ? 'Count unavailable' : low ? 'Restock' : 'In stock'}</span></td>
                    <td className="forecast-cell">
                      {category === 'UTENSILS' ? 'Not applicable' : forecast?.daysUntilThreshold === null
                        ? 'No sales in last 7 days'
                        : forecast
                          ? forecast.daysUntilThreshold === 0
                            ? <span className="forecast-now" title={`Average usage: ${forecast.averageDailyUsage} ${unit} per day`}>Restock now</span>
                            : <span title={`Average usage: ${forecast.averageDailyUsage} ${unit} per day`}>~{forecast.daysUntilThreshold} {forecast.daysUntilThreshold === 1 ? 'day' : 'days'}</span>
                          : '—'}
                    </td>
                  </tr>
                })}</tbody>
              </table>
              {items.length === 0 && <p className="empty-state">No inventory items found.</p>}
            </div>}
          </section>
          <p className="manager-foot">Items at or below their threshold need restocking. Forecasts estimate when ingredients reach that threshold using average usage over the last 7 days.</p>
        </main>
        <RoleSelectionScreen
          profile={auth.profile}
          loading={auth.loading}
          error={auth.error}
          onChooseRole={(role) => { void auth.chooseRole(role) }}
        />
      )}
      {screen === 'customer' && <CustomerScreen profile={auth.profile} />}
      {screen === 'manager' && <ManagerScreen profile={auth.profile} />}
    </div>
  )
}

export default App
