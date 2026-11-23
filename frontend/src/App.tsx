import { useState } from 'react'

import AppHeader from './components/AppHeader'
import { useGoogleSignIn } from './hooks/useGoogleSignIn'
import CustomerScreen from './screens/CustomerScreen'
import ManagerScreen from './screens/ManagerScreen'
import RoleSelectionScreen from './screens/RoleSelectionScreen'
import SignInScreen from './screens/SignInScreen'
import type { AppScreen } from './types'
import './App.css'

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
