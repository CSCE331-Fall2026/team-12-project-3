import type { MouseEvent } from 'react'

type AppHeaderProps = {
  signedIn: boolean
  onSignOut: () => void
}

export default function AppHeader({ signedIn, onSignOut }: AppHeaderProps) {
  const handleBrandClick = (event: MouseEvent<HTMLAnchorElement>) => {
    event.preventDefault()
    onSignOut()
  }

  return (
    <header className="topbar">
      <a
        className="brand"
        href="#home"
        onClick={handleBrandClick}
        aria-label="Panda Express home"
      >
        <span className="brand-wordmark"><strong>PANDA</strong><b>EXPRESS</b></span>
      </a>
      <span className="topbar-note">FRESHLY MADE</span>
      {signedIn && (
        <button className="signout" type="button" onClick={onSignOut}>
          Sign out
        </button>
      )}
    </header>
  )
}
