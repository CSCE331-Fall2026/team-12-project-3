export type UserRole = 'CUSTOMER' | 'MANAGER'
export type GoogleProfile = { email: string; googleId: string }
export type GoogleCredential = { credential: string }
export type InventoryCategory = 'FREEZER' | 'REFRIGERATOR' | 'PANTRY' | 'UTENSILS'
export type InventoryItem = { name: string; quantity?: number; restockThreshold: number; unit?: string }

export const inventoryCategories: { id: InventoryCategory; label: string }[] = [
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
