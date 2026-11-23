import type { IngredientForecast, InventoryCategory, InventoryItem } from '../types'
import { apiFetch } from './client'

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

export async function fetchIngredientForecasts(
  category: Exclude<InventoryCategory, 'UTENSILS'>,
): Promise<IngredientForecast[]> {
  const response = await apiFetch('/manager/ingredient-forecasts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ location: category }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Ingredient forecasts could not be loaded.')
  }
  return response.json() as Promise<IngredientForecast[]>
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
