export type AppScreen = 'signin' | 'role' | 'customer' | 'manager'
export type UserRole = 'CUSTOMER' | 'MANAGER'
export type GoogleProfile = { email: string; googleId: string }
export type GoogleCredential = { credential: string }
export type InventoryCategory = 'FREEZER' | 'REFRIGERATOR' | 'PANTRY' | 'UTENSILS'
export type InventoryItem = {
  name: string
  quantity?: number
  restockThreshold: number
  unit?: string
}
export type MenuCategory = 'POPULAR' | 'ENTREE' | 'SIDE' | 'APPETIZER' | 'DRINK'
export type MenuItem = {
  id: number
  name: string
  category: Exclude<MenuCategory, 'POPULAR'>
  size: 'SMALL' | 'MEDIUM' | 'LARGE' | string
  price: number
  imageURL?: string | null
  available: boolean
}
export type CustomerPaymentMethod = 'CREDIT_CARD' | 'DINING_DOLLARS'

export const customerMenuCategories: { id: MenuCategory; label: string }[] = [
  { id: 'POPULAR', label: 'Popular' },
  { id: 'ENTREE', label: 'Entrees' },
  { id: 'SIDE', label: 'Sides' },
  { id: 'APPETIZER', label: 'Appetizers' },
  { id: 'DRINK', label: 'Drinks' },
]

export const inventoryCategories: { id: InventoryCategory; label: string }[] = [
  { id: 'FREEZER', label: 'Freezer' },
  { id: 'REFRIGERATOR', label: 'Refrigerator' },
  { id: 'PANTRY', label: 'Pantry' },
  { id: 'UTENSILS', label: 'Utensils' },
]

export const menuCategoryArt: Record<Exclude<MenuCategory, 'POPULAR'>, string> = {
  ENTREE: '🥡',
  SIDE: '🍜',
  APPETIZER: '🥟',
  DRINK: '🥤',
}

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
