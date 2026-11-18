import { apiFetch } from './client'
import type { InventoryCategory, InventoryItem } from '../types'

export async function fetchInventoryItems(category: InventoryCategory): Promise<InventoryItem[]> {
  const response = await apiFetch('/manager/ingredients', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ location: category }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Inventory could not be loaded. Check that the backend is running.')
  }
  return response.json() as Promise<InventoryItem[]>
}

export async function restockItem(name: string, quantity: number, isUtensil: boolean) {
  const response = await apiFetch('/manager/restock/', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, quantity, isUtensil }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'The item could not be restocked.')
  }
}

export function displayUnit(unit: string | undefined, category: InventoryCategory) {
  if (category === 'UTENSILS') return 'ea'
  switch (unit?.toUpperCase()) {
    case 'KILOGRAMS': return 'kg'
    case 'LITERS': return 'L'
    case 'ITEMS': return 'ea'
    default: return unit ?? ''
  }
}
