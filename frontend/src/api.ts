export type SalesReportData = Record<string, Record<string, number>>
export type IngredientForecast = {
  name: string
  averageDailyUsage: number
  daysUntilThreshold: number | null
}
type InventoryCategory = 'FREEZER' | 'REFRIGERATOR' | 'PANTRY' | 'UTENSILS'

const backendUrl = import.meta.env.VITE_BACKEND_URL?.replace(/\/+$/, '')

export function apiFetch(path: string, init?: RequestInit) {
  if (!backendUrl) {
    return Promise.reject(new Error('Set BACKEND_URL in the frontend environment to connect to the backend.'))
  }
  const url = import.meta.env.DEV ? `/api${path}` : `${backendUrl}${path}`
  return fetch(url, init)
}

export async function fetchSalesReport(startDate: string, endDate: string): Promise<SalesReportData> {
  const response = await apiFetch('/manager/salesreport', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      start: `${startDate}T00:00:00`,
      end: `${endDate}T23:59:59`,
    }),
  })
  if (!response.ok) {
    const detail = (await response.text()).trim()
    throw new Error(detail || 'Sales report could not be loaded.')
  }
  return response.json() as Promise<SalesReportData>
}

export async function fetchIngredientForecasts(category: Exclude<InventoryCategory, 'UTENSILS'>) {
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
