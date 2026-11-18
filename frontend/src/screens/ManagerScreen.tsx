import CategoryTabs from '../components/inventory/CategoryTabs'
import InventoryTable from '../components/inventory/InventoryTable'
import { useInventory } from '../hooks/useInventory'
import { inventoryCategories } from '../types'
import type { GoogleProfile } from '../types'

type ManagerScreenProps = { profile: GoogleProfile | null }

export default function ManagerScreen({ profile }: ManagerScreenProps) {
  const inventory = useInventory()
  const categoryLabel = inventoryCategories.find((entry) => entry.id === inventory.category)?.label

  return (
    <main className="manager-page">
      <div className="manager-heading">
        <div><div className="eyebrow">Panda Express · Store operations</div><h1>Inventory</h1><p className="muted">Keep every wok ready for the next order.</p></div>
        <div className="manager-user"><span className="avatar">{profile?.email.slice(0, 1).toUpperCase()}</span><span>{profile?.email}</span></div>
      </div>
      <section className="inventory-section">
        <div className="inventory-header"><div><h2>{categoryLabel} inventory</h2><p className="muted">Current quantity compared with its restock threshold.</p></div></div>
        <CategoryTabs category={inventory.category} onSelect={inventory.selectCategory} />
        {inventory.loading && <p className="muted" role="status">Loading inventory…</p>}
        {inventory.error && <p className="error-message" role="alert">{inventory.error}</p>}
        {!inventory.loading && (
          <InventoryTable
            items={inventory.items}
            category={inventory.category}
            editingItem={inventory.editingItem}
            restockQuantity={inventory.restockQuantity}
            restocking={inventory.restocking}
            onEdit={(name) => { inventory.setEditingItem(name); inventory.setRestockQuantity(''); inventory.setError('') }}
            onQuantityChange={inventory.setRestockQuantity}
            onSave={(item) => { void inventory.saveRestock(item) }}
            onCancel={() => { inventory.setEditingItem(null); inventory.setRestockQuantity(''); inventory.setError('') }}
          />
        )}
      </section>
      <p className="manager-foot">Items at or below their threshold need restocking.</p>
    </main>
  )
}
