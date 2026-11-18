import { useEffect, useState } from 'react'
import { fetchInventoryItems, restockItem } from '../api/inventory'
import type { InventoryCategory, InventoryItem } from '../types'

export function useInventory() {
  const [items, setItems] = useState<InventoryItem[]>([])
  const [category, setCategory] = useState<InventoryCategory>('FREEZER')
  const [editingItem, setEditingItem] = useState<string | null>(null)
  const [restockQuantity, setRestockQuantity] = useState('')
  const [restocking, setRestocking] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    setLoading(true)
    setError('')
    fetchInventoryItems(category)
      .then((data) => { if (active) setItems(data) })
      .catch((cause: unknown) => {
        if (active) setError(cause instanceof Error ? cause.message : 'Inventory could not be loaded.')
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [category])

  const selectCategory = (nextCategory: InventoryCategory) => {
    setCategory(nextCategory)
    setEditingItem(null)
    setRestockQuantity('')
    setError('')
  }

  const saveRestock = async (item: InventoryItem) => {
    const quantity = Number(restockQuantity)
    if (!Number.isInteger(quantity) || quantity <= 0) {
      setError('Enter a whole number greater than zero.')
      return
    }

    setRestocking(true)
    setError('')
    try {
      await restockItem(item.name, quantity, category === 'UTENSILS')
      setItems(await fetchInventoryItems(category))
      setEditingItem(null)
      setRestockQuantity('')
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'The item could not be restocked.')
    } finally {
      setRestocking(false)
    }
  }

  return {
    items, category, selectCategory, editingItem, setEditingItem,
    restockQuantity, setRestockQuantity, restocking, saveRestock,
    loading, error, setError,
  }
}
