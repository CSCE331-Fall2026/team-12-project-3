import { inventoryCategories } from '../../types'
import type { InventoryCategory } from '../../types'

type CategoryTabsProps = {
  category: InventoryCategory
  onSelect: (category: InventoryCategory) => void
}

export default function CategoryTabs({ category, onSelect }: CategoryTabsProps) {
  return (
    <nav className="category-tabs" aria-label="Inventory categories">
      {inventoryCategories.map((entry) => {
        const isActive = category === entry.id
        return (
          <button
            key={entry.id}
            className={`category-tab ${isActive ? 'category-tab-active' : ''}`}
            type="button"
            aria-pressed={isActive}
            onClick={() => onSelect(entry.id)}
          >
            {entry.label}
          </button>
        )
      })}
    </nav>
  )
}
