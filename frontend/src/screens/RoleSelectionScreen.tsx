import StepIndicator from '../components/StepIndicator'
import type { GoogleProfile, UserRole } from '../types'

type RoleSelectionScreenProps = {
  profile: GoogleProfile | null
  loading: boolean
  error: string
  onChooseRole: (role: UserRole) => void
}

export default function RoleSelectionScreen({
  profile,
  loading,
  error,
  onChooseRole,
}: RoleSelectionScreenProps) {
  return (
    <main className="role-layout">
      <div className="role-content">
        <StepIndicator activeSteps={2} />
        <div className="eyebrow orange">One quick step</div>
        <h1>How will you use<br />Panda Express?</h1>
        <p className="muted">
          Choose a role for <strong>{profile?.email}</strong>. This is saved to your account.
        </p>
        <div className="role-options">
          <button
            className="role-option"
            type="button"
            onClick={() => onChooseRole('CUSTOMER')}
            disabled={loading}
          >
            <span className="role-symbol" aria-hidden="true">◉</span>
            <strong>Customer</strong>
            <span>Browse menu and place orders</span>
          </button>
          <button
            className="role-option"
            type="button"
            onClick={() => onChooseRole('MANAGER')}
            disabled={loading}
          >
            <span className="role-symbol" aria-hidden="true">▦</span>
            <strong>Manager</strong>
            <span>Track stock and restock needs</span>
          </button>
        </div>
        {loading && <p className="muted" role="status">Saving your role…</p>}
        {error && <p className="error-message" role="alert">{error}</p>}
      </div>
    </main>
  )
}
