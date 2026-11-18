import { useCallback, useEffect, useState } from 'react'
import { createUser, lookupRole, profileFromCredential } from '../api/auth'
import type { GoogleCredential, GoogleProfile, UserRole } from '../types'

type Screen = 'signin' | 'role' | 'customer' | 'manager'

export function useGoogleSignIn(screen: Screen, setScreen: (screen: Screen) => void) {
  const [profile, setProfile] = useState<GoogleProfile | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID

  const handleCredential = useCallback(async ({ credential }: GoogleCredential) => {
    setLoading(true)
    setError('')
    try {
      const nextProfile = profileFromCredential(credential)
      setProfile(nextProfile)
      const role = await lookupRole(nextProfile.email)
      setScreen(role === null ? 'role' : role === 'MANAGER' ? 'manager' : 'customer')
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Sign-in could not be completed.')
    } finally {
      setLoading(false)
    }
  }, [setScreen])

  useEffect(() => {
    if (screen !== 'signin' || !clientId) return

    const renderGoogleButton = () => {
      const mount = document.getElementById('google-button')
      if (!mount || !window.google || !clientId) return false
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
  }, [clientId, handleCredential, screen, setScreen])

  const chooseRole = async (selectedRole: UserRole) => {
    if (!profile) return
    setLoading(true)
    setError('')
    try {
      await createUser(profile, selectedRole)
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

  return { profile, loading, error, setError, handleCredential, chooseRole, signOut, clientId }
}
