import type { GoogleProfile } from '../types'

type CustomerScreenProps = { profile: GoogleProfile | null }

export default function CustomerScreen({ profile }: CustomerScreenProps) {
  return (
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
  )
}
