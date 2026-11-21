import { apiFetch } from './client'
import type { CustomerPaymentMethod, MenuCategory, MenuItem } from '../types'

const categoryValues: Record<Exclude<MenuCategory, 'POPULAR'>, string> = {
  ENTREE: 'ENTREE',
  SIDE: 'SIDE',
  APPETIZER: 'APPETIZER',
  DRINK: 'DRINK',
}

async function readMenuResponse(response: Response): Promise<MenuItem[]> {
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'The menu could not be loaded. Check that the backend is running.')
  }
  return response.json() as Promise<MenuItem[]>
}

export async function fetchPopularItems() {
  return readMenuResponse(await apiFetch('/customer/menuitems'))
}

export async function fetchCategoryItems(category: Exclude<MenuCategory, 'POPULAR'>) {
  return readMenuResponse(await apiFetch('/customer/menuitems', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ category: categoryValues[category] }),
  }))
}

export async function placeCustomerOrder(items: Record<number, number>, method: CustomerPaymentMethod, price: number) {
  const response = await apiFetch('/customer/order', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ items, method, price }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Your order could not be placed. Please try again.')
  }
}

