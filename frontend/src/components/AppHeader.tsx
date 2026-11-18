type AppHeaderProps = { signedIn: boolean; onSignOut: () => void }

export default function AppHeader({ signedIn, onSignOut }: AppHeaderProps) {
  return (
    <header className="topbar">
      <a className="brand" href="#home" onClick={(event) => { event.preventDefault(); onSignOut() }} aria-label="Panda Express home">
        <span className="brand-wordmark"><strong>PANDA</strong><b>EXPRESS</b></span>
      </a>
      <span className="topbar-note">STORE OPERATIONS</span>
      {signedIn && <button className="signout" type="button" onClick={onSignOut}>Sign out</button>}
    </header>
  )
}
