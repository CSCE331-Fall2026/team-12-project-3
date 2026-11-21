import StepIndicator from '../components/StepIndicator'

type SignInScreenProps = { clientId?: string; loading: boolean; error: string }

export default function SignInScreen({ clientId, loading, error }: SignInScreenProps) {
  return (
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
        <StepIndicator activeSteps={1} />
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
  )
}
