import { useState } from 'react'

import SalesReport from '../components/SalesReport'
import CategoryTabs from '../components/inventory/CategoryTabs'
import InventoryTable from '../components/inventory/InventoryTable'
import { useInventory } from '../hooks/useInventory'
import { inventoryCategories } from '../types'
import type { GoogleProfile, InventoryItem } from '../types'

type ManagerScreenProps = { profile: GoogleProfile | null }

export default function ManagerScreen({ profile }: ManagerScreenProps) {
  const [view, setView] = useState<'inventory' | 'sales'>('inventory')
  const inventory = useInventory()
  const categoryLabel = inventoryCategories.find((entry) => entry.id === inventory.category)?.label

  const handleEdit = (name: string) => {
    inventory.setEditingItem(name)
    inventory.setRestockQuantity('')
    inventory.setError('')
  }

  const handleCancel = () => {
    inventory.setEditingItem(null)
    inventory.setRestockQuantity('')
    inventory.setError('')
  }

  const handleSave = (item: InventoryItem) => {
    void inventory.saveRestock(item)
  }

  return (
    <main className="manager-page">
      <div className="manager-heading">
        <div>
          <div className="eyebrow">Panda Express</div>
          <h1>{view === 'inventory' ? "What's in stock" : 'Sales report'}</h1>
          <p className="muted">Manage your store and track sales performance.</p>
        </div>
        <div className="manager-user">
          <span className="avatar">{profile?.email.slice(0, 1).toUpperCase()}</span>
          <span>{profile?.email}</span>
        </div>
      </div>
      <nav className="manager-navigation" aria-label="Manager sections">
        <button
          className={view === 'inventory' ? 'manager-navigation-active' : ''}
          type="button"
          aria-current={view === 'inventory' ? 'page' : undefined}
          onClick={() => setView('inventory')}
        >
          Inventory
        </button>
        <button
          className={view === 'sales' ? 'manager-navigation-active' : ''}
          type="button"
          aria-current={view === 'sales' ? 'page' : undefined}
          onClick={() => setView('sales')}
        >
          Sales report
        </button>
      </nav>
      {view === 'inventory' ? (
      <>
      <section className="inventory-section">
        <div className="inventory-header">
          <div>
            <h2>{categoryLabel} in stock</h2>
            <p className="muted">See what's on hand and what needs a refill.</p>
          </div>
        </div>
        <CategoryTabs category={inventory.category} onSelect={inventory.selectCategory} />
        {inventory.loading && <p className="muted" role="status">Checking what's in stock…</p>}
        {inventory.error && <p className="error-message" role="alert">{inventory.error}</p>}
        {!inventory.loading && (
          <InventoryTable
            items={inventory.items}
            forecasts={inventory.forecasts}
            category={inventory.category}
            editingItem={inventory.editingItem}
            restockQuantity={inventory.restockQuantity}
            restocking={inventory.restocking}
            onEdit={handleEdit}
            onQuantityChange={inventory.setRestockQuantity}
            onSave={handleSave}
            onCancel={handleCancel}
          />
        )}
      </section>
      <p className="manager-foot">Anything marked Restock needs more.</p>
      </>
      ) : <SalesReport />}
    </main>
  )
}
