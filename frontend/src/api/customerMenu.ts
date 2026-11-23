import { apiFetch } from './client'
import type { CustomerPaymentMethod, MenuCategory, MenuItem } from '../types'

const categoryValues: Record<Exclude<MenuCategory, 'POPULAR'>, string> = {
  ENTREE: 'ENTREE',
  SIDE: 'SIDE',
  APPETIZER: 'APPETIZER',
  DRINK: 'DRINK',
}

async function responseDetail(response: Response) {
  const body = (await response.text()).trim()
  if (!body) return ''

  try {
    const parsed: unknown = JSON.parse(body)
    if (parsed && typeof parsed === 'object') {
      const record = parsed as Record<string, unknown>
      const details = Array.isArray(record.details)
        ? record.details.map((detail) => typeof detail === 'string' ? detail : JSON.stringify(detail)).join('; ')
        : ''
      const message = [record.message, record.title, details].find((value) => typeof value === 'string' && value.trim())
      if (typeof message === 'string') return message.trim()
    }
  } catch {
    // Plain text error responses are common in the development backend.
  }

  return body.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim().slice(0, 400)
}

function isGenericServerMessage(detail: string) {
  return /^(server error|internal server error|internal server error\.)$/i.test(detail.trim())
}

async function request(path: string, init: RequestInit | undefined, action: string) {
  try {
    return await apiFetch(path, init)
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error(`Could not connect to the ordering server while ${action}. Check that the backend is running, then try again.`)
    }
    throw error
  }
}

async function throwHttpError(response: Response, action: 'loading the menu' | 'placing your order'): Promise<never> {
  const detail = await responseDetail(response)
  const loadingMenu = action === 'loading the menu'
  if (response.status >= 500 && isGenericServerMessage(detail)) {
    if (loadingMenu) {
      throw new Error(`The menu server encountered an unexpected problem (HTTP ${response.status}) and did not return a specific cause. Refresh the page to try again. If this continues, check the backend logs.`)
    }
    throw new Error(`The server encountered an unexpected problem while ${action} (HTTP ${response.status}) and did not return a specific cause. Your order is still in the cart. Please try again; if this keeps happening, check the backend logs.`)
  }
  const requestLabel = action === 'loading the menu' ? 'load the menu' : 'place your order'
  if (detail) throw new Error(`Could not ${requestLabel} (HTTP ${response.status}): ${detail}`)
  if (response.status >= 500) {
    if (loadingMenu) {
      throw new Error(`The menu server encountered an unexpected problem (HTTP ${response.status}) but returned no details. Refresh the page to try again. If this continues, check the backend logs.`)
    }
    throw new Error(`The server encountered an unexpected problem while ${action} (HTTP ${response.status}) and returned no details. Your order is still in the cart. Please try again; if this keeps happening, check the backend logs.`)
  }
  throw new Error(`${action[0].toUpperCase()}${action.slice(1)} failed (HTTP ${response.status}). Please try again.`)
}

async function readMenuResponse(response: Response): Promise<MenuItem[]> {
  if (!response.ok) await throwHttpError(response, 'loading the menu')
  return response.json() as Promise<MenuItem[]>
}

export async function fetchPopularItems() {
  return readMenuResponse(await request('/customer/menuitems', undefined, 'loading the menu'))
}

export async function fetchCategoryItems(category: Exclude<MenuCategory, 'POPULAR'>) {
  return readMenuResponse(await request('/customer/menuitems', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ category: categoryValues[category] }),
  }, 'loading the menu'))
}

export async function placeCustomerOrder(items: Record<string, number>, method: CustomerPaymentMethod, price: number) {
  const response = await request('/customer/order', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ items, method, price }),
  }, 'placing your order')
  if (!response.ok) await throwHttpError(response, 'placing your order')
}

