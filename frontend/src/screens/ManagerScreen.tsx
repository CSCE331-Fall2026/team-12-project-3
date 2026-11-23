import CategoryTabs from '../components/inventory/CategoryTabs'
import InventoryTable from '../components/inventory/InventoryTable'
import { useInventory } from '../hooks/useInventory'
import { inventoryCategories } from '../types'
import type { GoogleProfile, InventoryItem } from '../types'

type ManagerScreenProps = { profile: GoogleProfile | null }

export default function ManagerScreen({ profile }: ManagerScreenProps) {
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
          <h1>What's in stock</h1>
          <p className="muted">Keep every wok ready for the next order.</p>
        </div>
        <div className="manager-user">
          <span className="avatar">{profile?.email.slice(0, 1).toUpperCase()}</span>
          <span>{profile?.email}</span>
        </div>
      </div>
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
    </main>
  )
}
